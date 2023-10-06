package com.befrvnk.kite.template.component

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.img
import org.intellij.lang.annotations.Language

data class Image(
    val url: String,
    val caption: String,
) : Component {
    @Language("css")
    override val css: String = """
        .header-container {
            align-items: flex-start;
            border-radius: 8px;
            display: flex;
            justify-content: center;
            max-height: 300px;
            overflow: hidden;
            width: 100%;
            
            img {
                min-height: 100%;
                min-width: 100%;
            }
        }
    """.trimIndent()

    override val html: FlowContent.() -> Unit = {
        div(classes = "header-container") {
            img(classes = "header-image", src = url)
        }
    }

}