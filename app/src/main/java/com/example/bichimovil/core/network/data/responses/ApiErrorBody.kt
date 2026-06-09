package com.example.bichimovil.core.network.data.responses

/**
 * Estructura de error que devuelve la API
 * Ejemplo: { "error": "insufficient_funds", "message": "Saldo insuficiente" }
 */
data class ApiErrorBody(
    val error: String,
    val message: String
)