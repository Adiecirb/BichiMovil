package com.example.bichimovil.core.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton para crear y configurar la instancia de Retrofit
 * Incluye AuthInterceptor para inyectar Firebase token en cada request
 */
object RetrofitClient {

    private const val BASE_URL = "https://us-central1-bankapp-e47b0.cloudfunctions.net/api/"

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())  // ← Inyecta token Firebase
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY  // Log completo en Debug
            })
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val bankAPI: BankAPI by lazy {
        retrofit.create(BankAPI::class.java)
    }
}