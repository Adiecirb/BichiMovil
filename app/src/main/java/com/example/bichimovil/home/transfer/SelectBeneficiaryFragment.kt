package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
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
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.databinding.FragmentSelectBeneficiaryBinding
import com.example.bichimovil.home.BeneficiaryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Lista de beneficiarios (GET /beneficiaries) con:
 * - Búsqueda/filtrado en tiempo real (alias, nombre o cuenta)
 * - Tap: seleccionar para transferir
 * - Long press: eliminar (DELETE /beneficiaries/{id}) con confirmación
 */
class SelectBeneficiaryFragment : Fragment() {

    private var _binding: FragmentSelectBeneficiaryBinding? = null
    private val binding get() = _binding!!

    private val beneficiariesViewModel by activityViewModels<BeneficiariesViewModel>()
    private val transferViewModel by activityViewModels<TransferViewModel>()
    private lateinit var adapter: BeneficiaryAdapter

    private var allBeneficiaries: List<BeneficiaryResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBeneficiaryBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupSearch()
        observeBeneficiaries()
        observeDelete()
        setupClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        beneficiariesViewModel.listBeneficiaries()
    }

    private fun setupRecyclerView() {
        adapter = BeneficiaryAdapter(
            onClick = { beneficiario ->
                transferViewModel.selectBeneficiary(beneficiario)
                findNavController().navigate(
                    R.id.action_selectBeneficiary_to_montoTransferencia
                )
            },
            onLongClick = { beneficiario ->
                confirmDelete(beneficiario)
            }
        )

        binding.rvBeneficiarios.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@SelectBeneficiaryFragment.adapter
        }
    }

    /** Filtro dinámico en tiempo real sobre la lista que devolvió la API. */
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            applyFilter(text.toString())
        }
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) {
            allBeneficiaries
        } else {
            val q = query.trim().lowercase()
            allBeneficiaries.filter {
                it.alias.lowercase().contains(q) ||
                        "${it.name} ${it.lastName}".lowercase().contains(q) ||
                        it.accountNumber.contains(q)
            }
        }
        renderList(filtered)
    }

    private fun renderList(lista: List<BeneficiaryResponse>) {
        if (lista.isEmpty()) {
            binding.layoutEmptyBeneficiarios.visibility = View.VISIBLE
            binding.rvBeneficiarios.visibility = View.GONE
        } else {
            binding.layoutEmptyBeneficiarios.visibility = View.GONE
            binding.rvBeneficiarios.visibility = View.VISIBLE
        }
        adapter.submitList(lista)
    }

    private fun confirmDelete(beneficiario: BeneficiaryResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar beneficiario")
            .setMessage(
                "¿Eliminar a \"${beneficiario.alias}\" " +
                        "(${beneficiario.name} ${beneficiario.lastName})?"
            )
            .setPositiveButton("Eliminar") { _, _ ->
                beneficiariesViewModel.deleteBeneficiary(beneficiario.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun observeBeneficiaries() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                beneficiariesViewModel.beneficiariesState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            allBeneficiaries = state.data
                            applyFilter(binding.etSearch.text.toString())
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

    private fun observeDelete() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                beneficiariesViewModel.deleteBeneficiaryState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            Snackbar.make(
                                binding.root,
                                "Beneficiario eliminado",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            beneficiariesViewModel.clearDeleteState()
                            beneficiariesViewModel.listBeneficiaries()
                        }
                        is ResponseService.Error -> {
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG)
                                .show()
                            beneficiariesViewModel.clearDeleteState()
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
