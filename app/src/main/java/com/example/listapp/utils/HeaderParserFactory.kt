package com.example.listapp.utils

/**
 * Factory method to generate the headers using Factory design pattern
 */


interface HeaderParserFactory {
    fun createFromType(type: Int): HeaderParser
    fun getFromType(type: Int): HeaderParser
}

class StandardParserFactory : HeaderParserFactory {
    lateinit var dropDownHeader: DropDownHeader
    lateinit var nestedHeader: NestedHeader
    lateinit var imageHeader: ImageHeader
    lateinit var videoHeader: VideoHeader
    override fun createFromType(type: Int): HeaderParser {
        when (type) {
            11 -> {
                dropDownHeader = DropDownHeader(11)
                return dropDownHeader
            }

            22 -> {
                nestedHeader = NestedHeader(22)
                return nestedHeader
            }
            33 -> {
                imageHeader = ImageHeader(33)
                return imageHeader
            }
            44 -> {
                videoHeader = VideoHeader(44)
                return videoHeader
            }
            else -> throw Exception("I don't know how to deal with $type")
        }
    }
    override fun getFromType(type: Int) =
        when (type) {
            11 -> dropDownHeader
            22 -> nestedHeader
            33 -> imageHeader
            44 -> videoHeader
            else -> throw Exception("I don't know how to deal with $type")
        }
}