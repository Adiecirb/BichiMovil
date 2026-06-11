package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Simulador de inversiones.
 * El saldo disponible se consulta a la API (GET /account) y el monto a
 * invertir queda estrictamente limitado a ese saldo.
 */
data class InvestmentSimulation(
    val type: String,  // "creciente" o "pagare"
    val initialAmount: Long,
    val months: Int,
    val finalAmount: Long,
    val gain: Long,
    val percentageGain: Double,
    val monthlyData: List<MonthlyData>
)

data class MonthlyData(
    val month: Int,
    val value: Long
)

/** Resultado de procesar la solicitud de inversión. */
sealed class InvestResult {
    data class Exitosa(val amountCents: Long) : InvestResult()
    data class NoExitosa(val reason: String) : InvestResult()
}

class InvestmentViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _selectedInvestment = MutableStateFlow<String?>(null)
    val selectedInvestment: StateFlow<String?> = _selectedInvestment.asStateFlow()

    private val _simulationResult = MutableStateFlow<InvestmentSimulation?>(null)
    val simulationResult: StateFlow<InvestmentSimulation?> = _simulationResult.asStateFlow()

    private val _currentBalanceCents = MutableStateFlow<Long>(0)  // Saldo real (API)
    val currentBalanceCents: StateFlow<Long> = _currentBalanceCents.asStateFlow()

    private val _investResult = MutableStateFlow<InvestResult?>(null)
    val investResult: StateFlow<InvestResult?> = _investResult.asStateFlow()

    fun selectInvestment(type: String) {
        _selectedInvestment.value = type
    }

    /** Saldo real desde GET /account. */
    fun loadCurrentBalance() {
        viewModelScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> _currentBalanceCents.value = result.data.balance
                else -> Unit
            }
        }
    }

    /**
     * Procesa la solicitud de inversión validando contra el saldo REAL de la
     * cuenta (recién consultado a la API para evitar datos viejos).
     */
    fun invest(amountCents: Long) {
        viewModelScope.launch {
            when (val account = bankRepository.getAccount()) {
                is ResponseService.Success -> {
                    val saldo = account.data.balance
                    _currentBalanceCents.value = saldo
                    _investResult.value = when {
                        amountCents <= 0 ->
                            InvestResult.NoExitosa("El monto debe ser mayor a 0")
                        amountCents > saldo ->
                            InvestResult.NoExitosa(
                                "Saldo insuficiente. Disponible: ${saldo.toCurrencyMXN()}"
                            )
                        else -> InvestResult.Exitosa(amountCents)
                    }
                }
                is ResponseService.Error ->
                    _investResult.value = InvestResult.NoExitosa(account.message)
                else -> Unit
            }
        }
    }

    fun clearInvestResult() {
        _investResult.value = null
    }

    fun runSimulation(type: String, amount: Long, months: Int) {
        viewModelScope.launch {
            val result = when (type) {
                "creciente" -> simulateCreciente(amount, months)
                "pagare" -> simulatePagare(amount, months)
                else -> null
            }
            _simulationResult.value = result
        }
    }

    private fun simulateCreciente(initialAmount: Long, months: Int): InvestmentSimulation {
        val monthlyRate = 0.02
        val monthlyData = mutableListOf<MonthlyData>()

        var currentValue = initialAmount
        for (m in 1..months) {
            currentValue = (currentValue * (1 + monthlyRate)).toLong()
            monthlyData.add(MonthlyData(m, currentValue))
        }

        val finalAmount = currentValue
        val gain = finalAmount - initialAmount
        val percentageGain = (gain.toDouble() / initialAmount) * 100

        return InvestmentSimulation(
            type = "creciente",
            initialAmount = initialAmount,
            months = months,
            finalAmount = finalAmount,
            gain = gain,
            percentageGain = percentageGain,
            monthlyData = monthlyData
        )
    }

    private fun simulatePagare(initialAmount: Long, months: Int): InvestmentSimulation {
        val monthlyRate = 0.015
        val monthlyData = mutableListOf<MonthlyData>()

        for (m in 1..months) {
            val currentValue = (initialAmount * (1 + (monthlyRate * m))).toLong()
            monthlyData.add(MonthlyData(m, currentValue))
        }

        val finalAmount = (initialAmount * (1 + (monthlyRate * months))).toLong()
        val gain = finalAmount - initialAmount
        val percentageGain = (gain.toDouble() / initialAmount) * 100

        return InvestmentSimulation(
            type = "pagare",
            initialAmount = initialAmount,
            months = months,
            finalAmount = finalAmount,
            gain = gain,
            percentageGain = percentageGain,
            monthlyData = monthlyData
        )
    }
}
