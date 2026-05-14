package com.example.bichimovil.home.cards

import com.example.bichimovil.core.ResponseService

interface CardService {
    suspend fun getUserCards(userId: String): ResponseService<List<Card>>
}