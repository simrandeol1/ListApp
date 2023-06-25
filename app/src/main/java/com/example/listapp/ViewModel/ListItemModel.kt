package com.example.listapp.ViewModel

import androidx.annotation.IntDef
import androidx.room.Ignore

data class DropDownItem(val list: List<String>)
data class NestedItem(val listViewModel: ListViewModel)
class ImageItem
class VideoItem

class ListItemModel {

    companion object {

        @IntDef(DHEADER, NHEADER, IHEADER, VHEADER, DROPDOWN, NESTED, IMAGE, VIDEO)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ListType

        const val DHEADER = 11
        const val NHEADER = 22
        const val IHEADER = 33
        const val VHEADER = 44
        const val DROPDOWN = 1
        const val NESTED = 2
        const val IMAGE = 3
        const val VIDEO = 4
    }

    @ListType
    var type : Int

    var isExpanded : Boolean

    lateinit var dropDown : DropDownItem
    lateinit var nestedItem: NestedItem
    lateinit var imageItem: ImageItem
    lateinit var videoItem: VideoItem

    constructor(@ListType type : Int, dropDown : DropDownItem, isExpanded : Boolean = false){
        this.type = type
        this.dropDown = dropDown
        this.isExpanded = isExpanded
    }

    constructor(@ListType type : Int, isExpanded : Boolean = false){
        this.type = type
        this.isExpanded = isExpanded
    }

    constructor(@ListType type : Int, nestedItem : NestedItem, isExpanded : Boolean = false){
        this.type = type
        this.nestedItem = nestedItem
        this.isExpanded = isExpanded
    }
    @Ignore
    constructor(@ListType type : Int, imageItem: ImageItem, isExpanded : Boolean = false){
        this.type = type
        this.imageItem = imageItem
        this.isExpanded = isExpanded
    }
    @Ignore
    constructor(@ListType type : Int, videoItem: VideoItem, isExpanded : Boolean = false){
        this.type = type
        this.videoItem = videoItem
        this.isExpanded = isExpanded
    }
}