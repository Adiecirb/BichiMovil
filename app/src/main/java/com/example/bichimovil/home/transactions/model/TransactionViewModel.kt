package com.example.bichimovil.home.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.home.transactions.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val repository: TransactionRepository = TransactionRepository()
) : ViewModel() {

    private val _transactionsState =
        MutableStateFlow<ResponseService<List<Transaction>>?>(null)
    val transactionsState: StateFlow<ResponseService<List<Transaction>>?> =
        _transactionsState.asStateFlow()

    fun loadTransactions(userId: String) {
        viewModelScope.launch {
            _transactionsState.value = ResponseService.Loading
            _transactionsState.value = repository.getUserTransactions(userId)
        }
    }
}