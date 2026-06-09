package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bichimovil.core.BeneficiariesViewModel
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentSelectBeneficiaryBinding
import com.example.bichimovil.home.BeneficiaryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SelectBeneficiaryFragment : Fragment() {

    private var _binding: FragmentSelectBeneficiaryBinding? = null
    private val binding get() = _binding!!

    private val beneficiariesViewModel by activityViewModels<BeneficiariesViewModel>()
    private lateinit var adapter: BeneficiaryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBeneficiaryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadBeneficiaries()
        observeBeneficiaries()
        setupClickListeners()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = BeneficiaryAdapter()

        binding.rvBeneficiarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectBeneficiaryFragment.adapter
        }
    }

    private fun loadBeneficiaries() {
        beneficiariesViewModel.listBeneficiaries()
    }

    private fun observeBeneficiaries() {
        viewLifecycleOwner.lifecycleScope.launch {
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
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is ResponseService.Loading -> Unit

                    null -> Unit
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnAddBeneficiario.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Agregar beneficiario pendiente",
                Snackbar.LENGTH_SHORT
            ).show()
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