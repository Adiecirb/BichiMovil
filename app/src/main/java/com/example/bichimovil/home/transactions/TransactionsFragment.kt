package com.example.bichimovil.home.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentTransactionsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        setupClickListeners()
        observeTransactions()
        loadTransactions()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.btnMasHome.setOnClickListener {
            findNavController().navigate(R.id.investmentsFragment)
        }

        binding.btnTransferirHome.setOnClickListener {
            findNavController().navigate(R.id.selectBeneficiaryFragment)
        }

        binding.btnRetirarHome.setOnClickListener {
            findNavController().navigate(R.id.retiroFragment)
        }

    }

    private fun loadTransactions() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.loadTransactions(uid)
    }

    private fun observeTransactions() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.transactionsState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> Unit

                        is ResponseService.Success -> {
                            // Aquí después conectas RecyclerView
                        }

                        is ResponseService.Error -> {
                            Snackbar.make(
                                binding.root,
                                state.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        null -> Unit
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