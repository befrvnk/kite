package com.befrvnk.kite.builder.notion

import com.befrvnk.kite.block.Annotations
import com.befrvnk.kite.block.HeaderImage
import com.befrvnk.kite.block.Heading
import com.befrvnk.kite.block.HeadingType
import com.befrvnk.kite.block.Text
import com.befrvnk.kite.logger
import com.befrvnk.knotion.objects.Page
import com.befrvnk.knotion.objects.block.Block
import com.befrvnk.knotion.objects.block.Bookmark
import com.befrvnk.knotion.objects.block.Breadcrumb
import com.befrvnk.knotion.objects.block.BulletListItem
import com.befrvnk.knotion.objects.block.Callout
import com.befrvnk.knotion.objects.block.ChildDatabase
import com.befrvnk.knotion.objects.block.ChildPage
import com.befrvnk.knotion.objects.block.Code
import com.befrvnk.knotion.objects.block.Column
import com.befrvnk.knotion.objects.block.ColumnList
import com.befrvnk.knotion.objects.block.Divider
import com.befrvnk.knotion.objects.block.Embed
import com.befrvnk.knotion.objects.block.Equation
import com.befrvnk.knotion.objects.block.File
import com.befrvnk.knotion.objects.block.Heading1
import com.befrvnk.knotion.objects.block.Heading2
import com.befrvnk.knotion.objects.block.Heading3
import com.befrvnk.knotion.objects.block.Image
import com.befrvnk.knotion.objects.block.LinkPreview
import com.befrvnk.knotion.objects.block.NumberedListItem
import com.befrvnk.knotion.objects.block.Paragraph
import com.befrvnk.knotion.objects.block.Pdf
import com.befrvnk.knotion.objects.block.Quote
import com.befrvnk.knotion.objects.block.SyncedBlock
import com.befrvnk.knotion.objects.block.Table
import com.befrvnk.knotion.objects.block.TableOfContents
import com.befrvnk.knotion.objects.block.TableRow
import com.befrvnk.knotion.objects.block.Todo
import com.befrvnk.knotion.objects.block.Toggle
import com.befrvnk.knotion.objects.block.Video
import com.befrvnk.knotion.objects.other.ExternalFile
import com.befrvnk.knotion.objects.other.NotionFile
import com.befrvnk.knotion.objects.richtext.RichText
import com.befrvnk.knotion.objects.user.User
import com.befrvnk.knotion.objects.richtext.Annotations as KnotionAnnotations

internal data class NotionData(
    val path: String,
    val page: Page,
    val blocks: List<Block>,
    val user: User,
) {
    val id: String get() = page.id
    val title: String get() = page.properties.title?.title?.joinToString { it.plainText }!!
}

internal fun NotionData.toKsitePage(linksMapping: Map<String, String>): com.befrvnk.kite.Page {
    fun KnotionAnnotations.toAnnotations() = Annotations(
        bold = bold,
        italic = italic,
        strikethrough = strikethrough,
        underline = underline,
        code = code,
    )

    fun List<RichText>.toContentRichText(): List<Text> = map {
        // TODO: Only supports notion internal links; Support external links as well
        val pageId = it.href?.drop(1)
        val href = if (pageId != null) linksMapping[pageId] else null
        Text(
            annotations = it.annotations.toAnnotations(),
            plainText = it.plainText,
            href = href,
        )
    }

    fun Page.header(): List<com.befrvnk.kite.block.Block> {
        val apiTitle = properties.title?.title
        val title = if (apiTitle != null) {
            Heading(
                type = HeadingType.H1,
                richText = apiTitle.toContentRichText(),
            )
        } else null
        val headerImageUrl = cover?.url()
        val headerImage = if (headerImageUrl != null) {
            HeaderImage(headerImageUrl)
        } else null

        return listOfNotNull(headerImage, title)
    }

    val blocks = page.header() + blocks.mapNotNull {
        when (it) {
            is Bookmark -> null
            is Breadcrumb -> null
            is BulletListItem -> null
            is Callout -> null
            is ChildDatabase -> null
            is ChildPage -> null
            is Code -> null
            is Column -> null
            is ColumnList -> null
            is Divider -> null
            is Embed -> null
            is Equation -> null
            is File -> null
            is Heading1 -> Heading(
                type = HeadingType.H1,
                richText = it.heading.richText.toContentRichText(),
            )
            is Heading2 -> Heading(
                type = HeadingType.H2,
                richText = it.heading.richText.toContentRichText(),
            )
            is Heading3 -> Heading(
                type = HeadingType.H3,
                richText = it.heading.richText.toContentRichText(),
            )
            is Image -> {
                val image = it.image
                com.befrvnk.kite.block.Image(
                    url = image.url(),
                    caption = when (image) {
                        is NotionFile -> image.caption.toContentRichText()
                        is ExternalFile -> image.caption.toContentRichText()
                    }
                )
            }
            is LinkPreview -> null
            is NumberedListItem -> null
            is Paragraph -> com.befrvnk.kite.block.Paragraph(
                richText = it.paragraph.richText.toContentRichText()
            )
            is Pdf -> null
            is Quote -> null
            is SyncedBlock -> null
            is Table -> null
            is TableOfContents -> null
            is TableRow -> null
            is Todo -> null
            is Toggle -> null
            is Video -> null
        }
    }
    logger.debug { "$path - Creating Page" }
    return com.befrvnk.kite.Page(
        path = path,
        title = title,
        created = page.createdTime,
        edited = page.lastEditedTime,
        author = user.name,
        blocks = blocks,
    )
}