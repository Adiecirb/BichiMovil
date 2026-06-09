package com.example.bichimovil.core.network.data

/**
 * Respuesta de GET /account o POST /account
 */
data class AccountResponse(
    val id: String,
    val ownerId: String,
    val accountNumber: String,
    val balance: Long,  // En centavos
    val createdAt: FirebaseTimestamp
)