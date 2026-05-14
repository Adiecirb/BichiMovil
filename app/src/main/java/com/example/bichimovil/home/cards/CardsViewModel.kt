package com.example.bichimovil.home.cards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.ResponseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardsViewModel(
    private val repository: CardRepository = CardRepository()
) : ViewModel() {

    private val _cardsState =
        MutableStateFlow<ResponseService<List<Card>>?>(null)
    val cardsState: StateFlow<ResponseService<List<Card>>?> =
        _cardsState.asStateFlow()

    fun loadCards(userId: String) {
        viewModelScope.launch {
            _cardsState.value = ResponseService.Loading
            _cardsState.value = repository.getUserCards(userId)
        }
    }
}