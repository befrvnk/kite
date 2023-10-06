package com.befrvnk.kite.template

import com.befrvnk.kite.block.Block

interface KiteTemplate {
    fun build(blocks: List<Block>): String
}