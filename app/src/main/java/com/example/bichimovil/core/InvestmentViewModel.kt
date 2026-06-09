package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Simulador de inversiones
 * TODO: En futuro, conectar a API real
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

class InvestmentViewModel : ViewModel() {

    private val _selectedInvestment = MutableStateFlow<String?>(null)
    val selectedInvestment: StateFlow<String?> = _selectedInvestment.asStateFlow()

    private val _simulationResult = MutableStateFlow<InvestmentSimulation?>(null)
    val simulationResult: StateFlow<InvestmentSimulation?> = _simulationResult.asStateFlow()

    fun selectInvestment(type: String) {
        _selectedInvestment.value = type
    }

    fun runSimulation(type: String, amount: Long, months: Int) {
        viewModelScope.launch {
            // Simulación ficticia según tipo
            val result = when (type) {
                "creciente" -> simulateCreciente(amount, months)
                "pagare" -> simulatePagare(amount, months)
                else -> null
            }
            _simulationResult.value = result
        }
    }

    private fun simulateCreciente(initialAmount: Long, months: Int): InvestmentSimulation {
        // Tasa de interés simulada: 2% mensual (compuesto)
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
        // Pagaré: 1.5% mensual simple
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