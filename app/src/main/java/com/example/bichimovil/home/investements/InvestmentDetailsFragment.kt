package com.example.bichimovil.home.investments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.core.InvestmentViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentInvestmentDetailsBinding
import com.google.android.material.slider.Slider
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

        setupUI()
        setupSliders()
        observeSimulation()
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

    private fun setupSliders() {
        // Slider de Monto (10k a 500k)
        binding.sliderMonto.setLabelFormatter { value ->
            "$${(value * 1000).toLong()} MXN"
        }
        binding.sliderMonto.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val montoMXN = (value * 1000).toLong()
                binding.tvMonto.text = montoMXN.toCurrencyMXN()
                runSimulation()
            }
        }

        // Slider de Meses (1 a 60)
        binding.sliderMeses.setLabelFormatter { value ->
            "${value.toInt()} meses"
        }
        binding.sliderMeses.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                binding.tvMeses.text = "${value.toInt()} meses"
                runSimulation()
            }
        }

        // Valores iniciales
        binding.sliderMonto.value = 10f  // 10k
        binding.sliderMeses.value = 12f  // 12 meses
    }

    private fun runSimulation() {
        val montoMXN = (binding.sliderMonto.value * 1000).toLong()
        val meses = binding.sliderMeses.value.toInt()
        val type = investmentViewModel.selectedInvestment.value ?: return

        investmentViewModel.runSimulation(type, montoMXN, meses)
    }

    private fun observeSimulation() {
        viewLifecycleOwner.lifecycleScope.launch {
            investmentViewModel.simulationResult.collect { result ->
                if (result != null) {
                    binding.tvInversionInicial.text = result.initialAmount.toCurrencyMXN()
                    binding.tvMontoFinal.text = result.finalAmount.toCurrencyMXN()
                    binding.tvGanancia.text = result.gain.toCurrencyMXN()
                    binding.tvPorcentajeGanancia.text = String.format("%.2f%%", result.percentageGain)

                    // Cambiar color según ganancia
                    val color = if (result.gain > 0) {
                        android.graphics.Color.GREEN
                    } else {
                        android.graphics.Color.RED
                    }
                    binding.tvGanancia.setTextColor(color)

                    // TODO: Dibujar gráfica con resultado.monthlyData
                    drawChart(result.monthlyData)
                }
            }
        }
    }

    private fun drawChart(monthlyData: List<Any>) {
        // TODO: Implementar gráfica (usar Recharts en web o AndroidX Charts)
        // Por ahora solo mostrar datos en tabla/lista
    }

    private fun setupClickListeners() {
        binding.btnInvertir.setOnClickListener {
            // Simulación: mostrar confirmación
            android.widget.Toast.makeText(
                requireContext(),
                "Inversión simulada (no real)",
                android.widget.Toast.LENGTH_SHORT
            ).show()
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