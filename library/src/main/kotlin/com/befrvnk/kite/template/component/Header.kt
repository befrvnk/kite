package com.befrvnk.kite.template.component

import com.befrvnk.kite.template.NavigationItem
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.header
import kotlinx.html.li
import kotlinx.html.nav
import kotlinx.html.ul
import org.intellij.lang.annotations.Language

data class Header(
    val title: String,
    val navigationItems: List<NavigationItem>,
) : Component {
    @Language("css")
    override val css: String = """
        .header {
            position: sticky;
            top: 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-direction: row;
            min-height: var(--header-height);
            backdrop-filter: saturate(180%) blur(5px);
            background-color: hsla(0, 0%, 100%, 0.8);
            width: 100%;
            padding-right: 24px;
            padding-left: 24px;
            
            > a {
                color: var(--link-color-hover);
                font-size: 1.5em;
                font-weight: 600;
                text-decoration: none;
            }
            
            > a:hover {
                color: var(--link-color-hover);
            }
            
            > nav > ul {
                display: flex;
                flex-direction: row;
                gap: 0.5em;
                list-style-type: none;
            }
            
            > nav > ul > li > a {
                color: var(--link-color);
                padding: 8px 12px;
                text-decoration: none;
            }
            
            > nav > ul > li > a:hover {
                color: var(--link-color-hover);
            }
        }
    """.trimIndent()

    override val html: FlowContent.() -> Unit = {
        header(classes = "header") {
            a {
                href = "/"
                +title
            }
            nav {
                ul {
                    navigationItems.forEach {
                        li {
                            a {
                                href = it.url
                                +it.title
                            }
                        }
                    }
                }
            }
        }
    }
}