package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.TransferViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentConfirmTransferBinding
import kotlinx.coroutines.launch

class ConfirmTransferFragment : Fragment() {

    private var _binding: FragmentConfirmTransferBinding? = null
    private val binding get() = _binding!!

    private val transferViewModel by activityViewModels<TransferViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmTransferBinding.inflate(inflater, container, false)

        observeTransferResult()
        setupClickListeners()

        return binding.root
    }

    private fun observeTransferResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            transferViewModel.transferState.collect { state ->
                if (state is ResponseService.Success) {
                    val transaction = state.data
                    binding.tvMonto.text = transaction.amount.toCurrencyMXN()
                    binding.tvDescripcion.text = transaction.description ?: "Sin descripción"
                    binding.tvEstado.text = "Transferencia Exitosa"
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            // Volver al Home (limpiar backstack del flujo de transferencia)
            findNavController().popBackStack(R.id.transactionsFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}