package com.befrvnk.kite.template.component

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h1
import org.intellij.lang.annotations.Language

data class Title(
    val text: String,
) : Component {
    @Language("css")
    override val css: String = ""
        override val html: FlowContent.() -> Unit = {
        div {
            h1 {
                +text
            }
        }
    }
}