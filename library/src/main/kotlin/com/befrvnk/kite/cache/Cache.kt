package com.befrvnk.kite.cache

import com.befrvnk.kite.Page

interface Cache <T> {
    fun getEntry(index: String): T?
    suspend fun cachePage(page: Page): Page
}