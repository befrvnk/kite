package com.befrvnk.kite.template.component

import com.befrvnk.kite.block.Annotations
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.span
import org.intellij.lang.annotations.Language

data class Text(
    val annotations: Annotations,
    val plainText: String,
    val href: String?,
) : Component {
    @Language("css")
    override val css: String = ""
    override val html: FlowContent.() -> Unit = {
        if (href == null) {
            span {
                +plainText
            }
        } else {
            a(href = href) {
                +plainText
            }
        }
    }
}
