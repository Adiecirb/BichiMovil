package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.TransferViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentMontoTransferenciaBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MontoTransferenciaFragment : Fragment() {

    private var _binding: FragmentMontoTransferenciaBinding? = null
    private val binding get() = _binding!!

    private val transferViewModel by activityViewModels<TransferViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMontoTransferenciaBinding.inflate(inflater, container, false)

        loadBeneficiaryInfo()
        loadAccount()
        setupValidation()
        setupClickListeners()

        return binding.root
    }

    private fun loadBeneficiaryInfo() {
        val beneficiary = transferViewModel.selectedBeneficiary.value
        if (beneficiary != null) {
            binding.tvBeneficiario.text = "${beneficiary.name} ${beneficiary.lastName}"
            binding.tvBanco.text = "Banco: ${beneficiary.alias}"
            binding.tvCuenta.text = "*${beneficiary.accountNumber.takeLast(4)}"
        }
    }

    private fun loadAccount() {
        transferViewModel.loadCurrentBalance()

        viewLifecycleOwner.lifecycleScope.launch {
            transferViewModel.currentAccount.collect { saldo ->
                binding.tvSaldoDisponible.text = "Saldo disponible: ${saldo.toCurrencyMXN()}"
            }
        }
    }

    private fun setupValidation() {
        binding.etMonto.addTextChangedListener { montoText ->
            val monto = montoText.toString()
            val montoCents = if (monto.isNotEmpty()) {
                (monto.toDoubleOrNull() ?: 0.0 * 100).toLong()
            } else {
                0
            }

            val error = transferViewModel.validateAmount(montoCents)
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
                binding.btnTransferir.isEnabled = false
            } else {
                binding.tvError.visibility = View.GONE
                binding.btnTransferir.isEnabled = true
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnTransferir.setOnClickListener {
            val montoText = binding.etMonto.text.toString()
            val montoCents = (montoText.toDoubleOrNull() ?: 0.0 * 100).toLong()
            val descripcion = binding.etConcepto.text.toString()

            val beneficiaryId = transferViewModel.selectedBeneficiary.value?.id
            if (beneficiaryId != null) {
                transferViewModel.transferMoney(beneficiaryId, montoCents, descripcion)
                observeTransferResult()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeTransferResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            transferViewModel.transferState.collect { state ->
                when (state) {
                    is ResponseService.Success -> {
                        // Ir a pantalla de confirmación
                        findNavController().navigate(
                            R.id.action_montoTransferencia_to_confirmTransfer
                        )
                    }
                    is ResponseService.Error -> {
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is ResponseService.Loading -> {
                        // Mostrar loader si quieres
                    }
                    null -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}