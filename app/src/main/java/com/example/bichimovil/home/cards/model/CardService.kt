package com.example.bichimovil.home.cards

import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.home.cards.model.Card

interface CardService {
    suspend fun getUserCards(userId: String): ResponseService<List<Card>>
}