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
        observeTransactions()
        loadTransactions()
        return binding.root
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
                        is ResponseService.Loading -> {
                            // mostrar loader
                        }
                        is ResponseService.Success -> {
                            // state.data es tu List<Transaction>
                            // aquí conectarás tu RecyclerView después
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(
                                binding.root,
                                state.error,
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