package com.example.bichimovil.home.transfer

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bichimovil.R
import com.example.bichimovil.core.BeneficiariesViewModel
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.TransferViewModel
import com.example.bichimovil.databinding.FragmentSelectBeneficiaryBinding
import com.example.bichimovil.home.BeneficiaryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SelectBeneficiaryFragment : Fragment() {

    private var _binding: FragmentSelectBeneficiaryBinding? = null
    private val binding get() = _binding!!

    private val beneficiariesViewModel by activityViewModels<BeneficiariesViewModel>()
    private val transferViewModel by activityViewModels<TransferViewModel>()
    private lateinit var adapter: BeneficiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBeneficiaryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeBeneficiaries()
        setupClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        // Recarga al volver de "Agregar beneficiario"
        beneficiariesViewModel.listBeneficiaries()
    }

    private fun setupRecyclerView() {
        adapter = BeneficiaryAdapter { beneficiario ->
            // Selecciona y continúa al monto
            transferViewModel.selectBeneficiary(beneficiario)
            findNavController().navigate(
                R.id.action_selectBeneficiary_to_montoTransferencia
            )
        }

        binding.rvBeneficiarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectBeneficiaryFragment.adapter
        }
    }

    private fun observeBeneficiaries() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                beneficiariesViewModel.beneficiariesState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            val beneficiarios = state.data
                            if (beneficiarios.isEmpty()) {
                                binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
                                binding.rvBeneficiarios.visibility = View.GONE
                            } else {
                                binding.layoutEmptyBeneficiarios.visibility = View.GONE
                                binding.rvBeneficiarios.visibility = View.VISIBLE
                                adapter.submitList(beneficiarios)
                            }
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_SHORT)
                                .show()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddBeneficiario.setOnClickListener {
            findNavController().navigate(
                R.id.action_selectBeneficiaryFragment_to_addBeneficiaryFragment
            )
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
