package com.evo.security.api

import com.evo.security.model.AnalysisResponse
import com.evo.security.model.UrlSubmissionRequest
import com.evo.security.model.SecurityAnalysisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VirusTotalApi {
    @GET("VirusTotal/hash/{hash}")
    suspend fun checkFileHash(@Path("hash") hash: String): Response<SecurityAnalysisResponse>

    @POST("VirusTotal/url")
    suspend fun submitUrl(@Body request: UrlSubmissionRequest): Response<AnalysisResponse>

    @GET("VirusTotal/analysis/{analysisId}")
    suspend fun getAnalysisResult(@Path("analysisId") analysisId: String): Response<SecurityAnalysisResponse>
}