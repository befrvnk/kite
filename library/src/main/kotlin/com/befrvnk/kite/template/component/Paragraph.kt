package com.befrvnk.kite.template.component

import com.befrvnk.kite.block.Text
import kotlinx.html.FlowContent
import kotlinx.html.p
import org.intellij.lang.annotations.Language
import com.befrvnk.kite.template.component.Text as TextComponent

data class Paragraph(
    val richText: List<Text>,
) : Component {
    @Language("css")
    override val css: String = ""
    override val html: FlowContent.() -> Unit = {
        p {
            richText.map { TextComponent(it.annotations, it.plainText, it.href) }
                .forEach { text ->
                    text.html(this)
                }
        }
    }

}