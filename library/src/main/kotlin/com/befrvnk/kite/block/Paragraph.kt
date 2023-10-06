package com.befrvnk.kite.block

data class Paragraph(
    val richText: List<Text>,
) : Block {
    val plainText: String get() = richText.joinToString { it.plainText }
}