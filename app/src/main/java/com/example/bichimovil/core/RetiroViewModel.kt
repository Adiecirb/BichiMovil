package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar retiros simulados
 * NOTA: Los retiros son SIMULADOS, no consume API real
 * Solo genera un PIN ficticio y muestra confirmación
 */
class RetiroViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _currentAccount = MutableStateFlow<Long>(0)  // Saldo en centavos
    val currentAccount: StateFlow<Long> = _currentAccount.asStateFlow()

    private val _retiroPin = MutableStateFlow<String>("")  // PIN simulado
    val retiroPin: StateFlow<String> = _retiroPin.asStateFlow()

    private val _retiroConfirmed = MutableStateFlow<Boolean>(false)
    val retiroConfirmed: StateFlow<Boolean> = _retiroConfirmed.asStateFlow()

    fun loadCurrentBalance() {
        viewModelScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> {
                    _currentAccount.value = result.data.balance
                }
                is ResponseService.Error -> {
                    // Handle error
                }
                is ResponseService.Loading -> {}
            }
        }
    }

    fun generateRetiroPin(amountCents: Long): String {
        // Generar PIN simulado de 6 dígitos
        val pin = (100000..999999).random().toString()
        _retiroPin.value = pin
        return pin
    }

    fun confirmRetiro(amountCents: Long) {
        // Simulación: solo marcamos como confirmado
        // En un app real, aquí se haría una llamada a API de retiro
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