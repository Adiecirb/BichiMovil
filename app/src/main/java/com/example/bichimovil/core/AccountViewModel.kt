package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.network.data.AccountResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar operaciones de Account
 * Usado en Home para mostrar saldo
 */
class AccountViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _accountState = MutableStateFlow<ResponseService<AccountResponse>?>(null)
    val accountState: StateFlow<ResponseService<AccountResponse>?> = _accountState.asStateFlow()

    fun createAccount() {
        viewModelScope.launch {
            _accountState.value = ResponseService.Loading
            _accountState.value = bankRepository.createAccount()
        }
    }

    fun getAccount() {
        viewModelScope.launch {
            _accountState.value = ResponseService.Loading
            _accountState.value = bankRepository.getAccount()
        }
    }

    fun fundAccount(amountCents: Long) {
        viewModelScope.launch {
            _accountState.value = ResponseService.Loading
            _accountState.value = bankRepository.fundAccount(amountCents)
        }
    }
}