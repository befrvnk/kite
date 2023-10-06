package com.befrvnk.kite.builder.notion

import com.befrvnk.kite.Page
import com.befrvnk.kite.logger
import com.befrvnk.knotion.Knotion
import com.befrvnk.knotion.objects.block.ChildPage
import kotlinx.datetime.Instant

internal class NotionDataLoader(integrationToken: String) {

    private val knotion = Knotion(integrationToken)

    suspend fun lastEdited(page: NotionPage): Instant {
        val id = page.url.notionId()
        val notionPage = knotion.pagesEndpoint.retrievePage(id).body()!!
        return notionPage.lastEditedTime
    }

    suspend fun loadData(page: NotionPage): List<Page> {
        logger.debug { "${page.path} - Loading data" }
        val id = page.url.notionId()
        val notionDataList = retrieveNotionData(
            pageId = id,
            isRoot = true,
            parentPath = page.path,
        )
        return notionDataList.map { it.toKsitePage(notionDataList.linksMapping()) }
    }

    private fun String.notionId(): String = split("/").last().split("-").last()

    private fun List<NotionData>.linksMapping(): Map<String, String> {
        val linksMapping = mutableMapOf<String, String>()
        forEach {
            val id = it.id.replace("-", "")
            linksMapping[id] = it.path
        }
        return linksMapping
    }

    private suspend fun retrieveNotionData(
        pageId: String,
        isRoot: Boolean,
        parentPath: String,
    ): List<NotionData> {
        logger.debug { "Loading data for $pageId" }
        val notionBlocks = knotion.blocksEndpoint.retrieveBlockChildren(pageId).body()!!
        val notionPage = knotion.pagesEndpoint.retrievePage(pageId).body()!!
        val user = knotion.usersEndpoint.retrieveUser(notionPage.createdBy.id).body()!!
        val title = notionPage.properties.title?.title?.joinToString { it.plainText }!!
        val path = if (isRoot) parentPath else {
            "$parentPath/$title"
                .replace("//", "/")
                .replace(" ", "-")
                .lowercase()
        }
        val notionDataFromSubpages = notionBlocks.results
            .mapNotNull { if (it is ChildPage) it.id else null }
            .flatMap {
                retrieveNotionData(
                    pageId = it,
                    isRoot = false,
                    parentPath = path,
                )
            }
        return listOf(
            NotionData(
                path = path,
                page = notionPage,
                blocks = notionBlocks.results,
                user = user,
            )
        ) + notionDataFromSubpages
    }
}
