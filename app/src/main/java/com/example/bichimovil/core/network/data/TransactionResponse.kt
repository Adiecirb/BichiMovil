package com.example.bichimovil.core.network.data

/**
 * Respuesta de POST /transaction o GET /transaction
 */
data class TransactionResponse(
    val id: String,
    val ownerId: String,
    val fromAccount: String,
    val toAccount: String,
    val toBeneficiaryId: String? = null,
    val amount: Long,  // En centavos
    val description: String? = null,
    val status: String,  // "completed", etc.
    val date: FirebaseTimestamp,
    val direction: String? = null  // "in" o "out" (solo en GET /transaction)
)