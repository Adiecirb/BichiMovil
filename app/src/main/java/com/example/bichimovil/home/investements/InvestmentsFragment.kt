package com.example.bichimovil.home.investments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.InvestmentViewModel
import com.example.bichimovil.databinding.FragmentInvestmentsBinding

class InvestmentsFragment : Fragment() {

    private var _binding: FragmentInvestmentsBinding? = null
    private val binding get() = _binding!!

    private val investmentViewModel by activityViewModels<InvestmentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentsBinding.inflate(inflater, container, false)

        setupClickListeners()

        return binding.root
    }

    private fun setupClickListeners() {
        // Inversión Creciente
        binding.cardCreciente.setOnClickListener {
            investmentViewModel.selectInvestment("creciente")
            findNavController().navigate(R.id.action_investments_to_investmentDetails)
        }

        // Pagaré
        binding.cardPagare.setOnClickListener {
            investmentViewModel.selectInvestment("pagare")
            findNavController().navigate(R.id.action_investments_to_investmentDetails)
        }

        // Volver
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}