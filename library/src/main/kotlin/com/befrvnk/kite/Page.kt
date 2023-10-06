package com.befrvnk.kite

import com.befrvnk.kite.block.Block
import kotlinx.datetime.Instant

data class Page(
    val path: String,
    val title: String,
    val created: Instant,
    val edited: Instant,
    val blocks: List<Block>,
    val author: String? = null,
    val html: String? = null,
)
