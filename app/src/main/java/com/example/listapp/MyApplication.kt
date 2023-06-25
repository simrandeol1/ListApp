package com.example.listapp

import android.app.Application
import android.net.Uri
import androidx.room.Room
import com.example.listapp.Dao.ListDao
import com.example.listapp.Database.AppDatabase
import com.example.listapp.Model.ChildItem
import com.example.listapp.Model.GrandchildItem
import com.example.listapp.Model.ParentItem
import com.example.listapp.Model.RowModel
import com.example.listapp.ViewModel.ListViewModel
import com.example.listapp.utils.DataSource
import dagger.Component
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Singleton


@Singleton
@Component(modules = [DataSource::class])

interface ApplicationComponent {

    fun inject(activity: MainActivity)
    fun inject(viewModel: ListViewModel)
}


class MyApplication: Application() {

    private val imageFiles: MutableList<Uri> = mutableListOf()
    private lateinit var videoFiles: Uri

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
    }
    fun addImageFile(file: Uri){
        imageFiles.add(file)
    }

    fun getImageFile(): MutableList<Uri>{
        return imageFiles
    }

    fun addVideoFile(file: Uri){
        videoFiles = file
    }

    fun getVideoFile(): Uri{
        return videoFiles
    }
}