package com.example.bichimovil.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * OkHttp Interceptor que inyecta el Firebase ID Token en cada request
 * a la API de banca. Corre antes de que el request salga del dispositivo.
 */
class AuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw IOException("No hay usuario autenticado.")

        // Tasks.await() corre en el thread pool de OkHttp, no en Main thread
        val token = try {
            // false = usa token cacheado; si expiró, Firebase lo renueva automáticamente
            Tasks.await(user.getIdToken(false)).token
        } catch (e: Exception) {
            throw IOException("No se pudo obtener el ID token de Firebase.", e)
        } ?: throw IOException("ID token vacío.")

        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}