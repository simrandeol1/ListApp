package com.example.listapp.ui

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.video.VideoCapture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import com.example.listapp.MyApplication
import com.example.listapp.databinding.CameraActivityBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity: AppCompatActivity() {

    private lateinit var binding: CameraActivityBinding

    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    lateinit var preview: Preview
    private lateinit var context: Context
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(binding.root, "The camera permission is necessary", Snackbar.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        context = this
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        binding.imageCaptureBtn.setOnClickListener {
            takePhoto()

        }
        binding.videoCaptureBtn.setOnClickListener {
            captureVideo()
        }
    }

    private fun startCamera() {
        preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.cameraView.surfaceProvider)
        }
        val recorder = Recorder.Builder()
            .setQualitySelector(
                QualitySelector.from(
                    Quality.HIGHEST,
                    FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                )
            )
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                if(intent.extras?.get("type") == "CAMERA") {
                    binding.imageCaptureBtn.visibility = View.VISIBLE
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                }
                else {
                    binding.videoCaptureBtn.visibility = View.VISIBLE
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture)
                }
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Use case binding failed")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {

        imageCapture?.let {
            val fileName = "JPEG_${System.currentTimeMillis()}"
            val file = File(externalMediaDirs[0], fileName)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            it.takePicture(
                outputFileOptions,
                imgCaptureExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        Log.i(ContentValues.TAG, "The image has been saved in ${file.toUri()}")
                        Snackbar.make(binding.root, "Image Clicked", Snackbar.LENGTH_SHORT).show()
                        MyApplication.instance.addImageFile(file.toUri())
//                        uploadImageToServer(file)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(
                            binding.root.context,
                            "Error taking photo",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(ContentValues.TAG, "Error taking photo:$exception")
                    }

                })
        }
    }

    private fun captureVideo() {

        // Check if the VideoCapture use case has been created: if not, do nothing.
//        val videoCapture = this.videoCapture ?: return

        binding.videoCaptureBtn.isEnabled = false

        // If there is an active recording in progress, stop it and release the current recording.
        // We will be notified when the captured video file is ready to be used by our application.
        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        // To start recording, we create a new recording session.
        // First we create our intended MediaStore video content object,
        // with system timestamp as the display name(so we could capture multiple videos).
        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(this.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture?.output
            ?.prepareRecording(context, mediaStoreOutputOptions)
            ?.apply {
                // Enable Audio for recording
                if (
                    PermissionChecker.checkSelfPermission(
                        context,
                        Manifest.permission.RECORD_AUDIO
                    ) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            ?.start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.videoCaptureBtn.apply {
                            text = "Stop"
                            isEnabled = true
                        }
                    }

                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg =
                                "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
    //                            parentFragmentManager.beginTransaction()
    //                                .replace(R.id.container, VideoViewFragment.newInstance(recordEvent.outputResults.outputUri))
    //                                .addToBackStack(null)
                            MyApplication.instance.addVideoFile(recordEvent.outputResults.outputUri)
    //                                .commit()
                            Log.d("simsim", msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e("simsim", "Video capture ends with error: ${recordEvent.error}")
                        }
                        binding.videoCaptureBtn.apply {
                            text = "start"
                            isEnabled = true
                        }
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        imgCaptureExecutor.shutdown()
    }

    private fun uploadImageToServer(imageFile: File) {
        val client = OkHttpClient()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", imageFile.name, imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("YOUR_SERVER_URL")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response
            }
        })
    }
}