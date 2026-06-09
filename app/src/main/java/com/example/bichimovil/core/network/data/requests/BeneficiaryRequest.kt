package com.example.bichimovil.core.network.data.requests

/**
 * Body para POST /beneficiaries o PUT /beneficiaries/{id}
 */
data class BeneficiaryRequest(
    val name: String,
    val lastName: String,
    val accountNumber: String,
    val alias: String
)