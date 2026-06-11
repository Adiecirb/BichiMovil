package com.example.bichimovil.home.retiro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.RetiroViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.core.toMoneyCents
import com.example.bichimovil.databinding.FragmentRetiroBinding
import kotlinx.coroutines.launch

class RetiroFragment : Fragment() {

    private var _binding: FragmentRetiroBinding? = null
    private val binding get() = _binding!!

    private val retiroViewModel by activityViewModels<RetiroViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRetiroBinding.inflate(inflater, container, false)

        loadAccount()
        setupValidation()
        setupClickListeners()

        return binding.root
    }

    private fun loadAccount() {
        // Saldo real desde la API
        retiroViewModel.loadCurrentBalance()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                retiroViewModel.currentAccount.collect { saldo ->
                    binding.tvSaldoDisponible.text =
                        "Saldo disponible: ${saldo.toCurrencyMXN()}"
                }
            }
        }
    }

    private fun setupValidation() {
        binding.etMonto.addTextChangedListener { montoText ->
            val montoCents = montoText.toString().toMoneyCents()

            val error = retiroViewModel.validateAmount(montoCents)
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
                binding.btnRetiro.isEnabled = false
            } else {
                binding.tvError.visibility = View.GONE
                binding.btnRetiro.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnRetiro.setOnClickListener {
            val montoCents = binding.etMonto.text.toString().toMoneyCents()
            if (montoCents <= 0) return@setOnClickListener

            retiroViewModel.generateRetiroPin(montoCents)
            findNavController().navigate(R.id.action_retiro_to_confirmRetiro)
        }
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
