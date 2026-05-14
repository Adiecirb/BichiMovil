package com.example.bichimovil.home.cards.model

data class Card(
    val id: String = "",
    val userId: String = "",
    val lastFourDigits: String = "",
    val cardHolder: String = "",
    val expirationDate: String = "",
    val balance: Double = 0.0,
    val type: String = "DEBIT"
)