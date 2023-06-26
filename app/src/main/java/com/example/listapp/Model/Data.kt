package com.example.listapp.Model

import androidx.annotation.IntDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

data class ParentItem(val title: String, val childItems: ChildItem)
data class ChildItem(val title: String, val grandchildItems: List<GrandchildItem>)
data class GrandchildItem(val name: String)
@Entity
class RowModel{

    companion object {

        @IntDef(COUNTRY, STATE, CITY)
        @Retention(AnnotationRetention.SOURCE)
        annotation class RowType

        const val COUNTRY = 0
        const val STATE = 1
        const val CITY = 2
    }

    @RowType
    @ColumnInfo
    var type : Int

    @ColumnInfo
    lateinit var language : String

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    lateinit var country : ParentItem

    lateinit var state : ChildItem

    lateinit var city : GrandchildItem

    @ColumnInfo
    var isExpanded : Boolean = false

    constructor(@RowType type : Int, country : ParentItem, language: String){
        this.type = type
        this.country = country
        this.language = language
    }
    @Ignore
    constructor(@RowType type : Int, state : ChildItem, isExpanded : Boolean = false){
        this.type = type
        this.state = state
        this.isExpanded = isExpanded
    }
    @Ignore
    constructor(@RowType type : Int, city: GrandchildItem, isExpanded : Boolean = false){
        this.type = type
        this.city = city
        this.isExpanded = isExpanded
    }

}