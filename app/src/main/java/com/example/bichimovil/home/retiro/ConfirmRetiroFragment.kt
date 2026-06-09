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
import com.example.bichimovil.databinding.FragmentConfirmRetiroBinding
import kotlinx.coroutines.launch

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

        observePinState()
        setupClickListeners()

        return binding.root
    }

    private fun observePinState() {
        viewLifecycleOwner.lifecycleScope.launch {
            retiroViewModel.retiroPin.collect { pin ->
                if (pin.isNotEmpty()) {
                    binding.tvPin.text = pin
                    binding.layoutPin.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnConfirmar.setOnClickListener {
            retiroViewModel.confirmRetiro(0)  // En retiro simulado, monto no se usa aquí
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