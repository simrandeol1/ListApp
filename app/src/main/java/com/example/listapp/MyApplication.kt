package com.example.listapp

import android.app.Application
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.example.listapp.ViewModel.ListViewModel
import com.example.listapp.utils.DataSource
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [DataSource::class])

interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(viewModel: ListViewModel)
}


class MyApplication: Application() {

    private val imageFiles: MutableList<String> = mutableListOf()
    private var videoFiles: MutableList<Uri> = mutableListOf()
    companion object {
        lateinit var instance: MyApplication
    }

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        instance = this
        applicationComponent = DaggerApplicationComponent
            .builder()
            .dataSource(DataSource(this))
            .build()
        val config: MutableMap<String, String> = HashMap<String, String>()
        config["dglfonwnl"] = "myCloudName"
        MediaManager.init(this, config)
    }
    fun addImageFile(file: String){
        imageFiles.add(file)
    }

    fun getImageFile(): MutableList<String>{
        return imageFiles
    }

    fun addVideoFile(file: Uri){
        videoFiles.add(0,file)
    }

    fun getVideoFile(): Uri{
        return videoFiles.get(0)
    }

    fun isVideoFileEmpty(): Boolean{
        return videoFiles.size==0
    }
}