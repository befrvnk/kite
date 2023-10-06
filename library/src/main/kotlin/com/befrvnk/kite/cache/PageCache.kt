package com.befrvnk.kite.cache

import com.befrvnk.kite.Page

class PageCache : Cache<Page> {
    private val pageCache: MutableMap<String, Page> = mutableMapOf()

    override fun getEntry(index: String) = pageCache[index]

    override suspend fun cachePage(page: Page): Page {
        pageCache[page.path] = page
        return page
    }

    fun isOutdated(page: Page): Boolean {
        val cacheEntry = pageCache[page.path]
        return cacheEntry == null || cacheEntry.edited != page.edited
    }
}

fun Cache<Page>.isOutdated(page: Page): Boolean {
    val cacheEntry = getEntry(page.path)
    return cacheEntry == null || cacheEntry.edited != page.edited
}