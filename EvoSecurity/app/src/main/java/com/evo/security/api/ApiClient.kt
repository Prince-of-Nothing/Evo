package com.evo.security.api

import android.content.Context
import com.evo.security.utils.NetworkUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    private var _virusTotalApi: VirusTotalApi? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        return try {
            // Create a trust manager that accepts all certificates
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getVirusTotalApi(context: Context): VirusTotalApi {
        if (_virusTotalApi == null) {
            val serverIp = NetworkUtils.getServerIpAddress(context)
            val baseUrl = "https://$serverIp:7205/"

            android.util.Log.d("ApiClient", "Initializing API client with base URL: $baseUrl")

            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getUnsafeOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            _virusTotalApi = retrofit.create(VirusTotalApi::class.java)
        }
        return _virusTotalApi!!
    }
}