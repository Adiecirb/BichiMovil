package com.example.bichimovil.home.retiro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.RetiroViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentConfirmRetiroBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Pantalla de datos del retiro (fragment_confirm_retiro):
 * muestra monto y clave; al confirmar ejecuta la transferencia al CAJERO
 * vía la API, que descuenta el saldo automáticamente.
 */
class ConfirmRetiroFragment : Fragment() {

    private var _binding: FragmentConfirmRetiroBinding? = null
    private val binding get() = _binding!!

    private val retiroViewModel by activityViewModels<RetiroViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmRetiroBinding.inflate(inflater, container, false)

        retiroViewModel.clearRetiroState()
        observeRetiroData()
        observeRetiroResult()
        setupClickListeners()

        return binding.root
    }

    private fun observeRetiroData() {
        viewLifecycleOwner.lifecycleScope.launch {
            retiroViewModel.retiroPin.collect { pin ->
                if (pin.isNotEmpty()) {
                    binding.tvPin.text = pin
                    binding.layoutPin.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            retiroViewModel.retiroAmountCents.collect { cents ->
                if (cents > 0) {
                    binding.tvMontoRetiro.text =
                        "Monto a retirar: ${cents.toCurrencyMXN()}"
                }
            }
        }
    }

    private fun observeRetiroResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                retiroViewModel.retiroState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            binding.btnConfirmar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            retiroViewModel.clearRetiroState()
                            Snackbar.make(
                                binding.root,
                                "Retiro exitoso. El monto fue descontado de tu saldo.",
                                Snackbar.LENGTH_LONG
                            ).show()
                            findNavController().popBackStack(
                                R.id.transactionsFragment, false
                            )
                        }
                        is ResponseService.Error -> {
                            binding.btnConfirmar.isEnabled = true
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                                .show()
                            retiroViewModel.clearRetiroState()
                        }
                        null -> binding.btnConfirmar.isEnabled = true
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirmar.setOnClickListener {
            retiroViewModel.confirmRetiro()
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
