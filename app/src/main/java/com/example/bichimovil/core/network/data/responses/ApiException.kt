package com.example.bichimovil.core.network.data.responses

/**
 * Excepción personalizada para errores de API
 * Contiene el código de error, mensaje y status HTTP
 */
class ApiException(
    val errorCode: String,
    message: String,
    val statusCode: Int
) : Exception(message)

/**
 * Helper para envolver llamadas a API y convertir excepciones HTTP en ApiException
 */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: retrofit2.HttpException) {
    val raw = e.response()?.errorBody()?.string()
    val apiError = try {
        com.google.gson.Gson().fromJson(raw, ApiErrorBody::class.java)
    } catch (_: Exception) {
        ApiErrorBody("unknown_error", e.message ?: "Error desconocido")
    }
    Result.failure(ApiException(apiError.error, apiError.message, e.code()))
} catch (e: Exception) {
    Result.failure(e)
}