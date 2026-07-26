package com.example.busbashlog.network

import com.example.busbashlog.model.VehicleListResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface BustimesApi {
    @GET("api/vehicles/")
    suspend fun searchVehicles(
        @Query("search") search: String,
        @Query("limit") limit: Int = 10,
        @Query("withdrawn") withdrawn: Boolean = false
    ): VehicleListResponse

    companion object {
        fun create(): BustimesApi {
            val json = Json { ignoreUnknownKeys = true; isLenient = true }
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
                .addInterceptor { chain ->
                    chain.proceed(
                        chain.request().newBuilder()
                            .header("User-Agent", "BusBashLog/1.0 (Android; educational)")
                            .header("Accept", "application/json")
                            .build()
                    )
                }
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl("https://bustimes.org/")
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(BustimesApi::class.java)
        }
    }
}
