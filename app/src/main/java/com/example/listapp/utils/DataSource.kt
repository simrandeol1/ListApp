package com.example.listapp.utils

import android.content.Context
import androidx.room.Room
import com.example.listapp.Database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
/**
 * dependency injection using dagger2 to use dao and database across the application
 */

@Module
class DataSource(applicationContext: Context){
    private var INSTANCE: AppDatabase
    private var applicationCtx = applicationContext

    init{
        INSTANCE = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "rowmodel"
        ).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun getDatabase(): AppDatabase {
        return INSTANCE
    }

    @Singleton
    @Provides
    fun getListDAO() = INSTANCE.listDao()

}