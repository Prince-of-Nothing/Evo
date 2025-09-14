package com.evo.security.model

import java.util.Date

data class RssItem(
    val title: String,
    val description: String,
    val link: String?,
    val pubDate: Date?,
    val source: String = "RSS Feed"
)