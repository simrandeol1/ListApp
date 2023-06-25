package com.example.listapp.utils

import android.content.Context
import androidx.room.Room
import com.example.listapp.Database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataSource(applicationContext: Context){
    private var INSTANCE: AppDatabase
    private var applicationCtx = applicationContext

    init{
        INSTANCE = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "rowmodel"
        ).allowMainThreadQueries().build()
//        if(getListDao().getAll().size == 0)
//            createDatabase()
    }

    @Singleton
    @Provides
    fun getDatabase(): AppDatabase {
        return INSTANCE
    }

    @Singleton
    @Provides
    fun getWeatherDAO() = INSTANCE.listDao()

}