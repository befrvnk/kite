package com.befrvnk.kite.block

data class Image(
    val url: String,
    val caption: List<Text>,
) : Block {
    val plainTextCaption: String get() = caption.joinToString { it.plainText }
}