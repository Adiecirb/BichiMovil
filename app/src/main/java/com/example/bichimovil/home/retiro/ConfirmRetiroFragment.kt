package com.example.bichimovil.home.retiro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.RetiroViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentConfirmRetiroBinding
import kotlinx.coroutines.launch

/**
 * Pantalla de éxito de retiro (fragment_confirm_retiro).
 * Muestra el monto solicitado y la clave de retiro generada.
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

        observeRetiroState()
        setupClickListeners()

        return binding.root
    }

    private fun observeRetiroState() {
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

    private fun setupClickListeners() {
        binding.btnConfirmar.setOnClickListener {
            retiroViewModel.confirmRetiro()
            findNavController().popBackStack(R.id.transactionsFragment, false)
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
