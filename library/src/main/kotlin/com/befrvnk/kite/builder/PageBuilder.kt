package com.befrvnk.kite.builder

import com.befrvnk.kite.Page

interface PageBuilder {
    suspend fun build(): List<Page>
}