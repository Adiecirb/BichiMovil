package com.example.bichimovil.home.transactions

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bichimovil.R
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.core.toMoneyCents
import com.example.bichimovil.databinding.FragmentTransactionsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Home: saldo, datos de cuenta, fondeo y últimos movimientos.
 * Todo el dato proviene de la API de Banca (nada hardcodeado).
 */
class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionsViewModel by viewModels()

    private val adapter = TransactionAdapter()

    private var balanceVisible = true
    private var currentBalanceCents: Long = 0
    private var cardLast4: String = "0000"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupClickListeners()
        observeAccount()
        observeTransactions()
        observeFunding()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Refresca saldo y movimientos al volver de transferir/retirar/fondear
        viewModel.loadHome()
    }

    private fun setupRecyclerView() {
        binding.rvMovimientos.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMovimientos.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnMasDatos.setOnClickListener {
            findNavController().navigate(R.id.accountDetailsFragment)
        }

        binding.ivToggleBalance.setOnClickListener {
            balanceVisible = !balanceVisible
            renderBalance()
        }

        binding.btnMasHome.setOnClickListener {
            findNavController().navigate(R.id.investmentsFragment)
        }

        binding.btnTransferirHome.setOnClickListener {
            findNavController().navigate(R.id.selectBeneficiaryFragment)
        }

        binding.btnRetirarHome.setOnClickListener {
            findNavController().navigate(R.id.retiroFragment)
        }

        binding.btnFondearHome.setOnClickListener {
            showFundDialog()
        }
    }

    /** Simulador de fondeo: pide un monto y llama PUT /account. */
    private fun showFundDialog() {
        val input = EditText(requireContext()).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Monto en pesos, ej. 500.00"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Fondear cuenta")
            .setMessage("Ingresa el monto a depositar (dinero ficticio):")
            .setView(input)
            .setPositiveButton("Depositar") { _, _ ->
                val cents = input.text.toString().toMoneyCents()
                if (cents <= 0) {
                    Snackbar.make(binding.root, "Ingresa un monto válido", Snackbar.LENGTH_SHORT)
                        .show()
                } else {
                    viewModel.fundAccount(cents)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun renderBalance() {
        if (balanceVisible) {
            binding.tvSaldo.text = currentBalanceCents.toCurrencyMXN()
            binding.tvCardDigits.text = "**** $cardLast4"
        } else {
            binding.tvSaldo.text = "$••••"
            binding.tvCardDigits.text = "**** ****"
        }
    }

    private fun observeAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            currentBalanceCents = state.data.balance
                            cardLast4 = state.data.accountNumber.takeLast(4)
                            renderBalance()
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionsState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            val movimientos = state.data
                            if (movimientos.isEmpty()) {
                                binding.layoutEmptyTransactions.visibility = View.VISIBLE
                                binding.rvMovimientos.visibility = View.GONE
                            } else {
                                binding.layoutEmptyTransactions.visibility = View.GONE
                                binding.rvMovimientos.visibility = View.VISIBLE
                                adapter.submitList(movimientos)
                            }
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun observeFunding() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fundState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            Snackbar.make(
                                binding.root,
                                "Depósito exitoso. Nuevo saldo: ${state.data.balance.toCurrencyMXN()}",
                                Snackbar.LENGTH_LONG
                            ).show()
                            viewModel.clearFundState()
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            viewModel.clearFundState()
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
