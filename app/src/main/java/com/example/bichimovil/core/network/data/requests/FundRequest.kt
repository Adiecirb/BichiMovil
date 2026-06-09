package com.example.bichimovil.core.network.data.requests

/**
 * Body para PUT /account (fondear cuenta)
 */
data class FundRequest(
    val amount: Long  // En centavos, entero positivo
)