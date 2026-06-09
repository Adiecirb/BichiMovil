package com.example.bichimovil.core.network.data.requests

/**
 * Body para POST /transaction (hacer transferencia)
 */
data class TransactionRequest(
    val toBeneficiaryId: String,
    val amount: Long,  // En centavos
    val description: String? = null
)