package com.evo.security.service

import android.content.Context
import android.util.Log
import com.evo.security.api.ApiClient
import com.evo.security.model.UrlSubmissionRequest
import com.evo.security.model.SecurityAnalysisResponse
import com.evo.security.model.ThreatLevel
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class VirusTotalService(private val context: Context) {

    private fun getApi() = ApiClient.getVirusTotalApi(context)

    suspend fun checkUrl(url: String): SecurityAnalysisResponse? {
        return try {
            Log.d("SecurityService", "Starting URL analysis workflow for: $url")

            val api = getApi()

            // Step 1: Submit URL for analysis
            Log.d("SecurityService", "Step 1: Submitting URL for analysis...")
            val submissionRequest = UrlSubmissionRequest(url)
            val submissionResponse = api.submitUrl(submissionRequest)

            if (!submissionResponse.isSuccessful) {
                Log.e("SecurityService", "URL submission failed: ${submissionResponse.code()} - ${submissionResponse.message()}")
                Log.e("SecurityService", "Response error body: ${submissionResponse.errorBody()?.string()}")
                return null
            }

            val analysisId = submissionResponse.body()?.analysisId
            if (analysisId == null) {
                Log.e("SecurityService", "No analysis ID received from submission")
                return null
            }

            Log.d("SecurityService", "URL submitted successfully. Analysis ID: $analysisId")

            // Step 2: Wait a moment and then retrieve results
            Log.d("SecurityService", "Step 2: Retrieving analysis results...")
            delay(1000) // Wait 1 second before retrieving results

            val resultsResponse = api.getAnalysisResult(analysisId)
            if (resultsResponse.isSuccessful) {
                Log.d("SecurityService", "Analysis results retrieved successfully: ${resultsResponse.body()}")
                resultsResponse.body()
            } else {
                Log.e("SecurityService", "Analysis results retrieval failed: ${resultsResponse.code()} - ${resultsResponse.message()}")
                Log.e("SecurityService", "Response error body: ${resultsResponse.errorBody()?.string()}")
                null
            }
        } catch (e: java.net.ConnectException) {
            Log.e("SecurityService", "Connection failed - server may be down or unreachable", e)
            null
        } catch (e: java.net.UnknownHostException) {
            Log.e("SecurityService", "Unknown host - check server IP address", e)
            null
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("SecurityService", "Connection timeout - server took too long to respond", e)
            null
        } catch (e: Exception) {
            Log.e("SecurityService", "Unexpected error in URL analysis workflow", e)
            null
        }
    }

    suspend fun checkFileHash(hash: String): SecurityAnalysisResponse? {
        return try {
            Log.d("SecurityService", "Checking file hash: $hash")

            val api = getApi()
            Log.d("SecurityService", "API client initialized for hash check, making request...")

            val response = api.checkFileHash(hash)
            if (response.isSuccessful) {
                Log.d("SecurityService", "Hash check successful: ${response.body()}")
                response.body()
            } else {
                Log.e("SecurityService", "Hash check failed: ${response.code()} - ${response.message()}")
                Log.e("SecurityService", "Response error body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: java.net.ConnectException) {
            Log.e("SecurityService", "Connection failed - server may be down or unreachable", e)
            null
        } catch (e: java.net.UnknownHostException) {
            Log.e("SecurityService", "Unknown host - check server IP address", e)
            null
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("SecurityService", "Connection timeout - server took too long to respond", e)
            null
        } catch (e: Exception) {
            Log.e("SecurityService", "Unexpected error checking file hash", e)
            null
        }
    }

    fun generateFileHash(fileBytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(fileBytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}