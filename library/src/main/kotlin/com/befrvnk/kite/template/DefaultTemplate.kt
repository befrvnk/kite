package com.befrvnk.kite.template

import com.befrvnk.kite.block.Block
import com.befrvnk.kite.block.HeaderImage
import com.befrvnk.kite.block.Heading
import com.befrvnk.kite.block.Image
import com.befrvnk.kite.block.Paragraph
import com.befrvnk.kite.block.Text
import com.befrvnk.kite.block.Title
import com.befrvnk.kite.template.component.Component
import com.befrvnk.kite.template.component.Footer
import com.befrvnk.kite.template.component.H1
import com.befrvnk.kite.template.component.Header
import kotlinx.html.HEAD
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.link
import kotlinx.html.stream.appendHTML
import kotlinx.html.title
import kotlinx.html.unsafe
import com.befrvnk.kite.template.component.HeaderImage as HeaderImageComponent
import com.befrvnk.kite.template.component.Image as ImageComponent
import com.befrvnk.kite.template.component.Paragraph as ParagraphComponent
import com.befrvnk.kite.template.component.Text as TextComponent
import com.befrvnk.kite.template.component.Title as TitleComponent

class DefaultTemplate(
    private val title: String,
    private val headerItems: List<NavigationItem> = listOf(),
    private val footerItems: List<NavigationItem> = listOf(),
) : KiteTemplate {

    private fun Block.toComponent(): Component {
        return when (this) {
            is HeaderImage -> HeaderImageComponent(url)
            is Heading -> H1(plainText)
            is Image -> ImageComponent(url, plainTextCaption)
            is Paragraph -> ParagraphComponent(richText)
            is Text -> TextComponent(annotations, plainText, href)
            is Title -> TitleComponent(text.plainText)
        }
    }

    override fun build(blocks: List<Block>): String = buildString {
        val header = Header(title, headerItems)
        val footer = Footer(title, footerItems)
        val components = blocks.map { it.toComponent() }
        append("<!DOCTYPE html>\n")
        appendHTML().html {
            head {
                title(this@DefaultTemplate.title)
                link(rel = "preconnect", href = "https://rsms.me/")
                link(rel = "stylesheet", href = "https://rsms.me/inter/inter.css")
                link(rel = "stylesheet", href = "/styles/modern-normalize.css", type = "text/css")
                styles(listOf(header, footer) + components)
            }
            body {
                header.html(this)
                div {
                    id = "root"
                    components.forEach { it.html(this) }
                }
                footer.html(this)
            }
        }
    }

    private fun HEAD.styles(components: List<Component>) {
        val css = components.distinctBy{ it.css }.joinToString("\n") { it.css }
        val mainCss = """
            :root {
                font-family: 'Inter', sans-serif;
                --header-height: 4em;
                --link-color: rgb(102, 102, 102);
                --link-color-hover: #000;
            }
            @supports (font-variation-settings: normal) {
                :root {
                    font-family: 'Inter var', sans-serif;
                }
            }
            #root {
                flex: 1;
                width: 100%;
                padding: 8px;
            }
            @media (min-width: 50em) {
                #root {
                    width: 50em;
                }
            }
            html {
                height: 100%;
            }
            body {
                display: flex;
                height: 100%;
                flex-direction: column;
                align-items: center;
                justify-content: start;
                min-height: 100%;
            }
        """.trimIndent()
        val style = buildString {
            appendLine("<style>")
            appendLine(mainCss)
            appendLine(css)
            appendLine("</style>")
        }
        unsafe { raw(style) }
    }
}
