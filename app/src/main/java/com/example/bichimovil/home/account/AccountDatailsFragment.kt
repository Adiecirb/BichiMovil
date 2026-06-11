package com.example.bichimovil.home.account

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.core.BankRepository
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentAccountDetailsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Credenciales de la cuenta. El número de cuenta y el saldo provienen
 * de GET /account; la CLABE y la tarjeta se derivan del accountNumber
 * real que asignó la API (no hay valores fijos).
 */
class AccountDetailsFragment : Fragment() {

    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!

    private val bankRepository = BankRepository.getInstance()

    private var numeroCuentaRaw = ""   // 10 dígitos, lo que entiende la API
    private var numeroCuenta = ""      // formateado solo para mostrar
    private var clabe = ""
    private var tarjeta = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        loadAccount()
        setupClickListeners()
    }

    private fun loadAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> {
                    val account = result.data

                    numeroCuentaRaw = account.accountNumber
                    numeroCuenta = formatGroups(account.accountNumber, 3)
                    clabe = buildClabe(account.accountNumber)
                    tarjeta = buildCardNumber(account.accountNumber)

                    binding.tvSaldo.text = account.balance.toCurrencyMXN()
                    binding.tvUltimosDigitos.text =
                        "**** ${account.accountNumber.takeLast(4)}"
                    binding.tvNumeroCuenta.text = numeroCuenta
                    binding.tvClabe.text = clabe
                    binding.tvTarjeta.text = tarjeta
                }
                is ResponseService.Error -> {
                    Snackbar.make(binding.root, result.message, Snackbar.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }
    }

    /** CLABE de 18 dígitos: banco+plaza (646180, STP) + cuenta + dígito control. */
    private fun buildClabe(accountNumber: String): String {
        val base = "646180" + accountNumber.padStart(11, '0')
        val control = base.sumOf { it.digitToInt() } % 10
        return base + control
    }

    /** Tarjeta de débito de 16 dígitos derivada del número de cuenta real. */
    private fun buildCardNumber(accountNumber: String): String {
        val raw = ("416916" + accountNumber.padStart(10, '0')).take(16)
        return formatGroups(raw, 4)
    }

    private fun formatGroups(value: String, size: Int): String =
        value.chunked(size).joinToString(" ")

    private fun setupClickListeners() {
        binding.btnCopyCuenta.setOnClickListener {
            // Se copia SIN espacios: es el valor que acepta la API
            copyText("Número de cuenta", numeroCuentaRaw)
        }

        binding.btnCopyClabe.setOnClickListener {
            copyText("Cuenta CLABE", clabe)
        }

        binding.btnCopyTarjeta.setOnClickListener {
            copyText("Tarjeta de débito", tarjeta)
        }

        binding.btnCompartir.setOnClickListener {
            if (numeroCuentaRaw.isEmpty()) return@setOnClickListener

            val texto = """
                BichiMovil - Cuenta Digital
                
                Número de cuenta: $numeroCuentaRaw
                Cuenta CLABE: $clabe
                Tarjeta de débito: $tarjeta
            """.trimIndent()

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, texto)
            }

            startActivity(Intent.createChooser(intent, "Compartir datos"))
        }
    }

    private fun copyText(label: String, value: String) {
        if (value.isEmpty()) return
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
        Snackbar.make(binding.root, "$label copiado", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
