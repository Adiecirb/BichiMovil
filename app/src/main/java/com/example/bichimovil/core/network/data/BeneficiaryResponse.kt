package com.example.bichimovil.core.network.data

/**
 * Respuesta de GET /beneficiaries, GET /beneficiaries/{id}, POST /beneficiaries
 */
data class BeneficiaryResponse(
    val id: String,
    val ownerId: String,
    val name: String,
    val lastName: String,
    val accountNumber: String,
    val alias: String,
    val createdAt: FirebaseTimestamp? = null  // POST devuelve esto, GET a veces no
)