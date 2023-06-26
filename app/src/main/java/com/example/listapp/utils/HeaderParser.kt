package com.example.listapp.utils

import com.example.listapp.ViewModel.DropDownItem
import com.example.listapp.ViewModel.ImageItem
import com.example.listapp.ViewModel.ListItemModel
import com.example.listapp.ViewModel.NestedItem
import com.example.listapp.ViewModel.VideoItem

/**
 * Header Classes and Subclasses
 */

interface HeaderParser {
    fun type(): Int
    fun addItemModel(): ListItemModel

    fun getHeader():String
}

class DropDownHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.DROPDOWN, DropDownItem())
    }

    override fun getHeader(): String {
        return "Drop Down View"
    }
}
class NestedHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.NESTED, NestedItem())
    }

    override fun getHeader(): String {
        return "Nested View"
    }
}
class ImageHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.IMAGE, ImageItem())
    }

    override fun getHeader(): String {
        return "Multiple Image View"
    }
}
class VideoHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.VIDEO, VideoItem())
    }

    override fun getHeader(): String {
        return "Video View"
    }
}