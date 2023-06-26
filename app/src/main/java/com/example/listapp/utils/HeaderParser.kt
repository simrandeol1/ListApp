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
}

class DropDownHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.DROPDOWN, DropDownItem())
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
}
class ImageHeader(typee: Int) : HeaderParser{
    var type = typee
    override fun type(): Int {
        return type
    }

    override fun addItemModel(): ListItemModel {
        return ListItemModel(ListItemModel.IMAGE, ImageItem())
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
}