package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para retiros sin tarjeta.
 * El saldo disponible se consulta SIEMPRE a la API (GET /account).
 * La API educativa no tiene endpoint de retiro, así que la "clave de retiro"
 * se genera localmente; el saldo se valida contra el saldo real de la API.
 */
class RetiroViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _currentAccount = MutableStateFlow<Long>(0)  // Saldo en centavos
    val currentAccount: StateFlow<Long> = _currentAccount.asStateFlow()

    private val _retiroPin = MutableStateFlow("")
    val retiroPin: StateFlow<String> = _retiroPin.asStateFlow()

    private val _retiroAmountCents = MutableStateFlow<Long>(0)
    val retiroAmountCents: StateFlow<Long> = _retiroAmountCents.asStateFlow()

    private val _retiroConfirmed = MutableStateFlow(false)
    val retiroConfirmed: StateFlow<Boolean> = _retiroConfirmed.asStateFlow()

    fun loadCurrentBalance() {
        viewModelScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> _currentAccount.value = result.data.balance
                else -> Unit
            }
        }
    }

    fun generateRetiroPin(amountCents: Long): String {
        _retiroAmountCents.value = amountCents
        val pin = (100000..999999).random().toString()
        _retiroPin.value = pin
        return pin
    }

    fun confirmRetiro() {
        _retiroConfirmed.value = true
    }

    fun validateAmount(amountCents: Long): String? {
        return when {
            amountCents <= 0 -> "El monto debe ser mayor a 0"
            amountCents > _currentAccount.value -> "Saldo insuficiente"
            else -> null
        }
    }
}
