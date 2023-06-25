package com.example.listapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.listapp.Converter.Converters
import com.example.listapp.Dao.ListDao
import com.example.listapp.Model.RowModel

@Database(entities = [RowModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listDao(): ListDao
}