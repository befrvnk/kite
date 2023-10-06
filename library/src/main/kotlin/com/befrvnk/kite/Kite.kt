package com.befrvnk.kite

import com.befrvnk.kite.builder.PageBuilder
import com.befrvnk.kite.cache.Cache
import com.befrvnk.kite.cache.ImageCache
import com.befrvnk.kite.cache.PageCache
import com.befrvnk.kite.cache.isOutdated
import com.befrvnk.kite.template.DefaultTemplate
import com.befrvnk.kite.template.KiteTemplate
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val logger = KotlinLogging.logger {}

@OptIn(ExperimentalCoroutinesApi::class)
class Kite(
    port: Int = 80,
    host: String = "0.0.0.0",
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val refreshTimeout: Duration = 15.minutes,
    private val pageBuilders: List<PageBuilder>,
    private val template: KiteTemplate = DefaultTemplate("Default Template", emptyList()),
    private val pageCache: Cache<Page> = PageCache(),
    private val imageCache: Cache<ByteArray> = ImageCache(),
) {

    private fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
        delay(initialDelay)
        while (true) {
            emit(Unit)
            delay(period)
        }
    }

    private suspend fun Page.cachePage(): Page {
        logger.debug { "$path - Caching page"}
        return pageCache.cachePage(this)
    }

    private suspend fun Page.cacheImages(): Page {
        logger.debug { "$path - Caching images"}
        return imageCache.cachePage(this)
    }

    private fun Page.generateHtml(template: KiteTemplate): Page {
        logger.debug { "$path - Generating HTML"}
        return copy(html = template.build(blocks))
    }

    @Suppress("unused")
    private fun dataFlow() = tickerFlow(refreshTimeout)
        .onEach { logger.debug { "Checking for updates" } }
        .flatMapConcat {
            pageBuilders.flatMapIndexed { index, pageBuilder ->
                logger.debug { "${index + 1}/${pageBuilders.size} page builder loading data" }
                pageBuilder.build()
            }.asFlow()
        }
        .filter { page -> pageCache.isOutdated(page) }
        .onEach { page ->
            page.cacheImages()
                .generateHtml(template)
                .cachePage()
        }
        .flowOn(ioDispatcher)

    private val netty = embeddedServer(
        factory = Netty,
        port = port,
        host = host,
    ) {
        routing {
            get("{...}") {
                logger.debug { "Requesting url ${context.request.uri}" }
                val uri = context.request.uri
                val page = pageCache.getEntry(uri)
                if (page?.html != null) {
                    context.respond(
                        TextContent(
                            text = page.html,
                            contentType = ContentType.Text.Html.withCharset(Charsets.UTF_8),
                            status = HttpStatusCode.OK,
                        )
                    )
                }
                val image = imageCache.getEntry(uri)
                if (image != null) {
                    context.respond(image)
                }
            }
            staticResources("/styles", "styles")
        }
    }

    fun start() = runBlocking(ioDispatcher) {
        launch {
            try {
                dataFlow().collect()
            } catch (error: Throwable) {
                logger.debug { error }
            }
        }
        val connector = netty.environment.connectors.first()
        logger.debug { "Ksite started at ${connector.host}:${connector.port}" }
        netty.start(wait = true)
    }
}
