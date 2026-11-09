package com.example.fluej.data.api

import android.util.Log
import com.example.fluej.data.supabase
import io.github.jan.supabase.gotrue.auth
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val http: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(40, TimeUnit.SECONDS)
            .addInterceptor { chain ->

                val token = supabase.auth.currentAccessTokenOrNull()

                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) {
                        header("Authorization", "Bearer $token")
                    }
                }.build()

                Log.d(
                    "ApiClient",
                    "URL: ${request.url} | Token presente: ${!token.isNullOrBlank()}"
                )

                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                    redactHeader("Authorization")
                }
            )
            .build()
    }

    val api: FluejApi by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(http)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(FluejApi::class.java)
    }
}