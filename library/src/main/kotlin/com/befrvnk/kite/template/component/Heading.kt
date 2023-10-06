package com.befrvnk.kite.template.component

import kotlinx.html.FlowContent
import kotlinx.html.h1
import org.intellij.lang.annotations.Language

data class H1(
    val text: String,
) : Component {
    @Language("css")
    override val css: String = ""
    override val html: FlowContent.() -> Unit = {
        h1 {
            +text
        }
    }
}