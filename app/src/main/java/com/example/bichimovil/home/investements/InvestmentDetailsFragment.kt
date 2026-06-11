package com.example.bichimovil.home.investments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.core.InvestResult
import com.example.bichimovil.core.InvestmentViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentInvestmentDetailsBinding
import kotlinx.coroutines.launch

class InvestmentDetailsFragment : Fragment() {

    private var _binding: FragmentInvestmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val investmentViewModel by activityViewModels<InvestmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentDetailsBinding.inflate(inflater, container, false)

        investmentViewModel.clearInvestResult()
        investmentViewModel.loadCurrentBalance()

        setupUI()
        setupSliders()
        observeSimulation()
        observeBalance()
        observeInvestResult()
        setupClickListeners()

        return binding.root
    }

    private fun setupUI() {
        val type = investmentViewModel.selectedInvestment.value

        binding.tvTitle.text = when (type) {
            "creciente" -> "Inversión Creciente"
            "pagare" -> "Pagaré"
            else -> "Inversión"
        }

        binding.tvDescription.text = when (type) {
            "creciente" -> "Interés compuesto: 2% mensual"
            "pagare" -> "Interés simple: 1.5% mensual"
            else -> ""
        }
    }

    /** Muestra el saldo real de la API junto a la descripción. */
    private fun observeBalance() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                investmentViewModel.currentBalanceCents.collect { saldo ->
                    val base = when (investmentViewModel.selectedInvestment.value) {
                        "creciente" -> "Interés compuesto: 2% mensual"
                        "pagare" -> "Interés simple: 1.5% mensual"
                        else -> ""
                    }
                    binding.tvDescription.text =
                        "$base\nSaldo disponible para invertir: ${saldo.toCurrencyMXN()}"
                }
            }
        }
    }

    private fun setupSliders() {
        binding.sliderMonto.setLabelFormatter { value ->
            "$${(value * 1000).toLong()} MXN"
        }
        binding.sliderMonto.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                val montoMXN = (value * 1000).toLong()
                binding.tvMonto.text = (montoMXN * 100).toCurrencyMXN()
                runSimulation()
            }
        }

        binding.sliderMeses.setLabelFormatter { value ->
            "${value.toInt()} meses"
        }
        binding.sliderMeses.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                binding.tvMeses.text = "${value.toInt()} meses"
                runSimulation()
            }
        }

        binding.sliderMonto.value = 10f
        binding.sliderMeses.value = 12f
        runSimulation()
    }

    /** Monto seleccionado en CENTAVOS (el slider está en miles de pesos). */
    private fun selectedAmountCents(): Long =
        (binding.sliderMonto.value * 1000).toLong() * 100

    private fun runSimulation() {
        val montoCents = selectedAmountCents()
        val meses = binding.sliderMeses.value.toInt()
        val type = investmentViewModel.selectedInvestment.value ?: return

        investmentViewModel.runSimulation(type, montoCents, meses)
    }

    private fun observeSimulation() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                investmentViewModel.simulationResult.collect { result ->
                    if (result != null) {
                        binding.tvInversionInicial.text = result.initialAmount.toCurrencyMXN()
                        binding.tvMontoFinal.text = result.finalAmount.toCurrencyMXN()
                        binding.tvGanancia.text = result.gain.toCurrencyMXN()
                        binding.tvPorcentajeGanancia.text =
                            String.format("%.2f%%", result.percentageGain)

                        val color = if (result.gain > 0) {
                            android.graphics.Color.parseColor("#2E7D32")
                        } else {
                            android.graphics.Color.parseColor("#C62828")
                        }
                        binding.tvGanancia.setTextColor(color)
                    }
                }
            }
        }
    }

    /** Mensajes explícitos de "Exitosa" / "No exitosa". */
    private fun observeInvestResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                investmentViewModel.investResult.collect { result ->
                    when (result) {
                        is InvestResult.Exitosa -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Inversión Exitosa")
                                .setMessage(
                                    "Tu inversión por ${result.amountCents.toCurrencyMXN()} " +
                                            "fue registrada correctamente."
                                )
                                .setPositiveButton("Aceptar") { _, _ ->
                                    findNavController().popBackStack()
                                }
                                .show()
                            investmentViewModel.clearInvestResult()
                        }
                        is InvestResult.NoExitosa -> {
                            AlertDialog.Builder(requireContext())
                                .setTitle("Inversión No Exitosa")
                                .setMessage(result.reason)
                                .setPositiveButton("Entendido", null)
                                .show()
                            investmentViewModel.clearInvestResult()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnInvertir.setOnClickListener {
            // Valida contra el saldo REAL de la cuenta (API)
            investmentViewModel.invest(selectedAmountCents())
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
