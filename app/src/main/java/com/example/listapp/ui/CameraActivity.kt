package com.example.listapp.ui

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.listapp.MyApplication
import com.example.listapp.R
import com.example.listapp.databinding.CameraActivityBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
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

        cameraPermissionResult.launch(Manifest.permission.CAMERA)

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

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder().build()
            val recorder = Recorder.Builder()
                .setQualitySelector(
                    QualitySelector.from(
                        Quality.HIGHEST,
                        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
                    )
                )
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
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
                        uploadFile(file.absolutePath)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Snackbar.make(binding.root, "Error taking photo", Snackbar.LENGTH_SHORT).show()
                        Log.d(ContentValues.TAG, "Error taking photo:$exception")
                    }

                })
        }
    }

    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        binding.videoCaptureBtn.isEnabled = false

        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }

        val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        name.timeZone = TimeZone.getTimeZone("GMT")
        val nameFormet = name.format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, nameFormet)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(this.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()

        recording = videoCapture.output
            .prepareRecording(context, mediaStoreOutputOptions)
            .apply {
                // Enable Audio for recording
                if (
                    PermissionChecker.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED
                ) {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
                when(recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.videoCaptureBtn.apply {
                            text = getString(R.string.stop_video)
                            isEnabled = true
                        }
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Video capture succeeded: ${recordEvent.outputResults.outputUri}"
//                            uploadVideoFile(getVideoFromUri(recordEvent.outputResults.outputUri))
                            MyApplication.instance.addVideoFile(recordEvent.outputResults.outputUri)
                        } else {
                            recording?.close()
                            recording = null
                        }
                        binding.videoCaptureBtn.apply {
                            text = getString(R.string.take_video)
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

    /**
     * function to upload image file to cloudinary server
     */

    fun uploadFile(filePath: String?) {
        val cloudinary = Cloudinary("cloudinary://599711957751898:kG9Sj_TUY_YN54m7LFHYTqbrFVM@dglfonwnl")
        val file = filePath?.let { File(it) }

        val uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
        val imageUrl = uploadResult["url"] as String
        MyApplication.instance.addImageFile(imageUrl)
    }
    private fun uploadVideoFile(videoFile: File?) {
        val cloudinary = Cloudinary("cloudinary://599711957751898:kG9Sj_TUY_YN54m7LFHYTqbrFVM@dglfonwnl")

        val uploadResult = cloudinary.uploader().uploadLarge(videoFile, ObjectUtils.emptyMap())
        val imageUrl = uploadResult["url"] as String
    }
    private fun getVideoFromUri(uri: Uri): File? {
        val filePath = getFilePathFromUri(uri)
        if (filePath != null) {
            return File(filePath)
        }
        return null
    }

    private fun getFilePathFromUri(uri: Uri): String? {
        var filePath: String? = null
        val scheme = uri.scheme
        if (scheme == ContentResolver.SCHEME_CONTENT) {
            val cursor = applicationContext.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                    filePath = it.getString(columnIndex)
                }
            }
        } else if (scheme == ContentResolver.SCHEME_FILE) {
            filePath = uri.path
        }
        return filePath
    }
}