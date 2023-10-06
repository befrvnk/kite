package com.befrvnk.kite.block

data class Heading(
    val type: HeadingType,
    val richText: List<Text>,
) : Block {
    val plainText: String get() = richText.joinToString { it.plainText }
}

enum class HeadingType {
    H1, H2, H3
}