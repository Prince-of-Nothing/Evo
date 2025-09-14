package com.evo.security.service

import android.util.Log
import com.evo.security.model.RssItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RssParserService {

    suspend fun parseRssFeed(feedUrl: String): List<RssItem> = withContext(Dispatchers.IO) {
        try {
            Log.d("RssParser", "Fetching RSS feed from: $feedUrl")

            val url = URL(feedUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 10000
            connection.readTimeout = 10000
            connection.setRequestProperty("User-Agent", "EvoSecurity RSS Reader")

            val inputStream: InputStream = connection.inputStream
            val items = parseXml(inputStream)
            inputStream.close()
            connection.disconnect()

            Log.d("RssParser", "Successfully parsed ${items.size} RSS items")
            items
        } catch (e: Exception) {
            Log.e("RssParser", "Error parsing RSS feed: $feedUrl", e)
            emptyList()
        }
    }

    private fun parseXml(inputStream: InputStream): List<RssItem> {
        val items = mutableListOf<RssItem>()

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = false
            val parser = factory.newPullParser()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentItem: RssItemBuilder? = null
            var currentTag = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        currentTag = parser.name.lowercase()
                        if (currentTag == "item") {
                            currentItem = RssItemBuilder()
                        }
                    }

                    XmlPullParser.TEXT -> {
                        currentItem?.let { item ->
                            val text = parser.text?.trim() ?: ""
                            if (text.isNotEmpty()) {
                                when (currentTag) {
                                    "title" -> item.title = cleanHtml(text)
                                    "description" -> item.description = cleanHtml(text)
                                    "link" -> item.link = text
                                    "pubdate" -> item.pubDate = parseDate(text)
                                }
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (parser.name.lowercase() == "item") {
                            currentItem?.build()?.let { rssItem ->
                                items.add(rssItem)
                            }
                            currentItem = null
                        }
                        currentTag = ""
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("RssParser", "Error parsing XML", e)
        }

        return items
    }

    private fun parseDate(dateString: String): Date? {
        val dateFormats = listOf(
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH),
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
        )

        for (format in dateFormats) {
            try {
                return format.parse(dateString)
            } catch (e: Exception) {
                // Try next format
            }
        }

        Log.w("RssParser", "Could not parse date: $dateString")
        return null
    }

    private fun cleanHtml(text: String): String {
        return text
            // Decode common HTML entities
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&amp;", "&")
            .replace("&apos;", "'")
            // Remove HTML tags using regex
            .replace(Regex("<[^>]*>"), "")
            // Clean up extra whitespace
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private class RssItemBuilder {
        var title: String = ""
        var description: String = ""
        var link: String? = null
        var pubDate: Date? = null

        fun build(): RssItem? {
            return if (title.isNotEmpty() && description.isNotEmpty()) {
                RssItem(
                    title = title,
                    description = description,
                    link = link,
                    pubDate = pubDate ?: Date()
                )
            } else {
                null
            }
        }
    }
}