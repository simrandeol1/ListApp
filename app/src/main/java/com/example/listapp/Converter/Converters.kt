package com.example.listapp.Converter

import android.text.TextUtils
import androidx.room.TypeConverter
import com.example.listapp.Model.ChildItem
import com.example.listapp.Model.GrandchildItem
import com.example.listapp.Model.ParentItem
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun StringToItem(string: String?): ParentItem? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, ParentItem::class.java)
    }

    @TypeConverter
    fun ItemeToString(parentItem: ParentItem?): String? {
        return Gson().toJson(parentItem)
    }
    @TypeConverter
    fun StringToCItem(string: String?): ChildItem? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, ChildItem::class.java)
    }

    @TypeConverter
    fun CItemeToString(parentItem: ChildItem?): String? {
        return Gson().toJson(parentItem)
    }
    @TypeConverter
    fun StringToGItem(string: String?): GrandchildItem? {
        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, GrandchildItem::class.java)
    }

    @TypeConverter
    fun GItemeToString(parentItem: GrandchildItem?): String? {
        return Gson().toJson(parentItem)
    }
}