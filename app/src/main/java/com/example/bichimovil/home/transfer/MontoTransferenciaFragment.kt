package com.example.bichimovil.home.transfer

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
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.TransferViewModel
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.core.toMoneyCents
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

        // Limpia cualquier resultado de una transferencia anterior
        transferViewModel.clearTransferState()

        loadBeneficiaryInfo()
        loadAccount()
        setupValidation()
        setupClickListeners()
        observeTransferResult()

        return binding.root
    }

    private fun loadBeneficiaryInfo() {
        val beneficiary = transferViewModel.selectedBeneficiary.value
        if (beneficiary != null) {
            binding.tvBeneficiario.text = "${beneficiary.name} ${beneficiary.lastName}"
            binding.tvBanco.text = beneficiary.alias
            binding.tvCuenta.text = "*${beneficiary.accountNumber.takeLast(4)}"
        }
    }

    private fun loadAccount() {
        transferViewModel.loadCurrentBalance()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferViewModel.currentAccount.collect { saldo ->
                    binding.tvSaldoDisponible.text =
                        "Saldo disponible: ${saldo.toCurrencyMXN()}"
                }
            }
        }
    }

    private fun setupValidation() {
        binding.etMonto.addTextChangedListener { montoText ->
            val montoCents = montoText.toString().toMoneyCents()

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
            val montoCents = binding.etMonto.text.toString().toMoneyCents()
            val descripcion = binding.etConcepto.text.toString()

            val beneficiaryId = transferViewModel.selectedBeneficiary.value?.id
            if (beneficiaryId != null && montoCents > 0) {
                binding.btnTransferir.isEnabled = false
                transferViewModel.transferMoney(beneficiaryId, montoCents, descripcion)
            }
        }
    }

    private fun observeTransferResult() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                transferViewModel.transferState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            findNavController().navigate(
                                R.id.action_montoTransferencia_to_confirmTransfer
                            )
                        }
                        is ResponseService.Error -> {
                            binding.btnTransferir.isEnabled = true
                            // El mensaje viene de la API (insufficient_funds, same_account...)
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
