package com.befrvnk.kite.block

data class Text(
    val annotations: Annotations,
    val plainText: String,
    val href: String?,
) : Block

data class Annotations(
    val bold: Boolean,
    val italic: Boolean,
    val strikethrough: Boolean,
    val underline: Boolean,
    val code: Boolean,
)