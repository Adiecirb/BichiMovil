package com.example.bichimovil.home.transactions.model

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val type: String = "DEBIT",
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)