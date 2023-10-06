package com.befrvnk.kite.template.component

import kotlinx.html.FlowContent

sealed interface Component {
    val css: String
    val html: FlowContent.() -> Unit
}