package com.befrvnk.kite.cache

import com.befrvnk.kite.Page
import com.befrvnk.kite.block.HeaderImage
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class ImageCache(
    private val client: HttpClient = HttpClient(CIO)
) : Cache<ByteArray> {
    private val imageCache: MutableMap<String, ByteArray> = mutableMapOf()

    override fun getEntry(index: String) = imageCache[index]

    override suspend fun cachePage(page: Page): Page {
        return page.copy(
            blocks = page.blocks.map { block ->
                when (block) {
                    is HeaderImage -> {
                        val imageName = page.title.replace(" ", "-").lowercase()
                        val cacheUrl = "/image/$imageName.jpg"
                        com.befrvnk.kite.logger.debug { "${page.path} - Transforming image ${block.url}" }
                        imageCache[cacheUrl] = transformHeaderImage(block.url)
                        com.befrvnk.kite.logger.debug { "${page.path} - Caching image ${block.url} as $cacheUrl" }
                        HeaderImage(cacheUrl)
                    }
                    else -> block
                }
            }
        )
    }

    private suspend fun transformHeaderImage(
        url: String,
        compression: Int = 90,
        width: Int = 800,
    ): ByteArray {
        val httpResponse: HttpResponse = client.get(url)
        val responseBody: ByteArray = httpResponse.body()
        val image = ImmutableImage.loader().fromBytes(responseBody).scaleToWidth(width)
        val writer = JpegWriter().withCompression(compression).withProgressive(true)
        return image.forWriter(writer).bytes()
    }
}