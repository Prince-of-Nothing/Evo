package com.evo.security.repository

import android.util.Log
import com.evo.security.model.FileCheckRequest
import com.evo.security.model.FileCheckResult
import com.evo.security.model.SecurityNews
import com.evo.security.model.SecuritySeverity
import com.evo.security.model.ThreatLevel
import com.evo.security.service.VirusTotalService
import com.evo.security.service.RssParserService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

class SecurityRepository(private val context: android.content.Context) {

    private val _securityNews = MutableStateFlow<List<SecurityNews>>(emptyList())
    val securityNews: StateFlow<List<SecurityNews>> = _securityNews.asStateFlow()
    private val virusTotalService = VirusTotalService(context)
    private val rssParserService = RssParserService()

    // Configure your RSS feed URL here
    private var rssFeedUrl = "https://stisc.gov.md/ro/rss.xml" // STISC Moldova security alerts

    fun setRssFeedUrl(url: String) {
        rssFeedUrl = url
    }

    init {
        loadSampleNews()
    }

    private fun loadSampleNews() {
        val sampleNews = listOf(
            SecurityNews(
                id = "1",
                title = "New Android Malware Campaign Detected",
                description = "Security researchers have identified a new strain of Android malware targeting banking apps. The malware disguises itself as legitimate applications and can steal sensitive financial information.",
                severity = SecuritySeverity.HIGH,
                timestamp = Date(System.currentTimeMillis() - 3600000), // 1 hour ago
                source = "EvoSecurity Labs",
                url = "https://example.com/news1"
            ),
            SecurityNews(
                id = "2",
                title = "Critical Chrome Security Update Available",
                description = "Google has released a critical security update for Chrome browser addressing multiple zero-day vulnerabilities. Users are advised to update immediately.",
                severity = SecuritySeverity.CRITICAL,
                timestamp = Date(System.currentTimeMillis() - 7200000), // 2 hours ago
                source = "Google Security",
                url = "https://example.com/news2"
            ),
            SecurityNews(
                id = "3",
                title = "Phishing Campaign Targets Remote Workers",
                description = "A sophisticated phishing campaign is targeting remote workers with fake VPN and collaboration tool emails. Be cautious of unexpected software installation requests.",
                severity = SecuritySeverity.MEDIUM,
                timestamp = Date(System.currentTimeMillis() - 14400000), // 4 hours ago
                source = "CyberAlert",
                url = "https://example.com/news3"
            ),
            SecurityNews(
                id = "4",
                title = "WiFi Security Best Practices Reminder",
                description = "With increased remote work, it's important to review WiFi security settings. Ensure WPA3 encryption and regular password updates for optimal security.",
                severity = SecuritySeverity.LOW,
                timestamp = Date(System.currentTimeMillis() - 28800000), // 8 hours ago
                source = "EvoSecurity Tips",
                url = "https://example.com/news4"
            )
        )

        _securityNews.value = sampleNews.sortedByDescending { it.timestamp }
    }

    suspend fun checkFileOrUrl(request: FileCheckRequest): FileCheckResult {
        return try {
            when {
                request.url != null -> {
                    // Check URL using VirusTotal API
                    val response = virusTotalService.checkUrl(request.url)
                    if (response != null) {
                        val isSafe = response.isSafe()
                        val threats = buildThreatList(response.malicious, response.suspicious)

                        FileCheckResult(
                            isSafe = isSafe,
                            threats = threats,
                            scanEngine = "Security Engine"
                        )
                    } else {
                        // Fallback if API fails
                        FileCheckResult(
                            isSafe = false,
                            threats = listOf("API unavailable - verification failed"),
                            scanEngine = "Security Engine (Failed)"
                        )
                    }
                }
                request.fileHash != null -> {
                    // Check file hash using VirusTotal API
                    val response = virusTotalService.checkFileHash(request.fileHash)
                    if (response != null) {
                        val isSafe = response.isSafe()
                        val threats = buildThreatList(response.malicious, response.suspicious)

                        FileCheckResult(
                            isSafe = isSafe,
                            threats = threats,
                            scanEngine = "Security Engine"
                        )
                    } else {
                        // Fallback if API fails
                        FileCheckResult(
                            isSafe = false,
                            threats = listOf("API unavailable - verification failed"),
                            scanEngine = "Security Engine (Failed)"
                        )
                    }
                }
                else -> {
                    // Fallback for invalid requests
                    FileCheckResult(
                        isSafe = false,
                        threats = listOf("Invalid request - no URL or hash provided"),
                        scanEngine = "Security Engine"
                    )
                }
            }
        } catch (e: Exception) {
            // Error handling
            FileCheckResult(
                isSafe = false,
                threats = listOf("Scan error: ${e.message}"),
                scanEngine = "Security Engine (Error)"
            )
        }
    }

    private fun buildThreatList(maliciousCount: Int, suspiciousCount: Int): List<String> {
        val threats = mutableListOf<String>()

        if (maliciousCount > 0) {
            threats.add("Malicious content detected by $maliciousCount security engines")
        }
        if (suspiciousCount > 0) {
            threats.add("Suspicious activity flagged by $suspiciousCount security engines")
        }

        return threats.ifEmpty {
            emptyList()
        }
    }

    fun addSecurityNews(news: SecurityNews) {
        val currentNews = _securityNews.value.toMutableList()
        currentNews.add(0, news) // Add to top
        _securityNews.value = currentNews.sortedByDescending { it.timestamp }
    }

    suspend fun loadRssNews(feedUrl: String? = null) {
        try {
            Log.d("SecurityRepository", "Loading RSS news...")
            val urlToUse = feedUrl ?: rssFeedUrl
            val rssItems = rssParserService.parseRssFeed(urlToUse)

            val newsItems = rssItems.map { rssItem ->
                SecurityNews(
                    id = UUID.randomUUID().toString(),
                    title = rssItem.title,
                    description = rssItem.description,
                    severity = determineSeverity(rssItem.title, rssItem.description),
                    timestamp = rssItem.pubDate ?: Date(),
                    source = rssItem.source,
                    url = rssItem.link
                )
            }

            _securityNews.value = newsItems.sortedByDescending { it.timestamp }
            Log.d("SecurityRepository", "Loaded ${newsItems.size} news items from RSS")
        } catch (e: Exception) {
            Log.e("SecurityRepository", "Error loading RSS news", e)
            // Keep existing news if RSS fails
        }
    }

    private fun determineSeverity(title: String, description: String): SecuritySeverity {
        val text = "$title $description".lowercase()
        return when {
            // Critical - Romanian and English
            text.contains("critical") || text.contains("urgent") || text.contains("zero-day") ||
            text.contains("alertă") || text.contains("critic") || text.contains("urgente") -> SecuritySeverity.CRITICAL

            // High - Romanian and English
            text.contains("high") || text.contains("severe") || text.contains("exploit") ||
            text.contains("breach") || text.contains("ransomware") || text.contains("atac") ||
            text.contains("phishing") || text.contains("atenție") -> SecuritySeverity.HIGH

            // Medium - Romanian and English
            text.contains("medium") || text.contains("moderate") || text.contains("vulnerability") ||
            text.contains("patch") || text.contains("dezinformare") || text.contains("vigilență") ||
            text.contains("securitate") -> SecuritySeverity.MEDIUM

            else -> SecuritySeverity.LOW
        }
    }

    fun refreshNews() {
        // This will be called from UI to refresh RSS feed
        // The actual loading should be done with coroutines from the calling code
    }
}