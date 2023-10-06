package com.befrvnk.kite.builder.notion

import com.befrvnk.kite.Page
import com.befrvnk.kite.builder.PageBuilder

class NotionPageBuilder(
    integrationToken: String,
    configure: NotionPageBuilder.() -> Unit,
) : PageBuilder {
    private val notionPages: MutableList<NotionPage> = mutableListOf()
    private val dataloader = NotionDataLoader(integrationToken)

    init {
        configure()
    }

    fun page(path: String, url: String) {
        notionPages.add(NotionPage(path, url))
    }

    override suspend fun build(): List<Page> = notionPages.flatMap { dataloader.loadData(it) }
}

data class NotionPage(
    val path: String,
    val url: String,
)