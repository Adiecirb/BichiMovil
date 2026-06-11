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
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla de éxito de transferencia (fragment_confirm_transfer).
 * Muestra los datos REALES de la transacción que devolvió la API.
 */
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

        showTransactionData()
        setupClickListeners()

        return binding.root
    }

    private fun showTransactionData() {
        viewLifecycleOwner.lifecycleScope.launch {
            transferViewModel.transferState.collect { state ->
                if (state is ResponseService.Success) {
                    val tx = state.data
                    val fmt = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))

                    binding.tvMonto.text = tx.amount.toCurrencyMXN()
                    binding.tvCodigo.text = tx.id
                    binding.tvFecha.text = fmt.format(tx.date.toDate())
                    binding.tvDescripcion.text =
                        tx.description?.takeIf { it.isNotBlank() } ?: "Sin descripción"
                    binding.tvEstado.text = when (tx.status) {
                        "completed" -> "Completada"
                        else -> tx.status
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnVolver.setOnClickListener {
            transferViewModel.clearTransferState()
            findNavController().popBackStack(R.id.transactionsFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
