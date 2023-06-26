package com.example.listapp.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.listapp.Model.RowModel

@Dao
interface ListDao {

    @Query("SELECT * FROM rowmodel")
    fun getAll(): MutableList<RowModel>

    @Insert
    fun insert(users: RowModel)

    @Query("SELECT language FROM rowmodel")
    fun getLanguages(): MutableList<String>


}