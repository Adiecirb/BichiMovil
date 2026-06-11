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
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentAccountDetailsBinding
import com.google.android.material.snackbar.Snackbar

class AccountDetailsFragment : Fragment() {

    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!

    private val numeroCuenta = "104 6895 5232"
    private val clabe = "137180104689552325"
    private val tarjeta = "4169 1606 1355 5870"

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

        binding.tvNumeroCuenta.text = numeroCuenta
        binding.tvClabe.text = clabe
        binding.tvTarjeta.text = tarjeta

        binding.btnCopyCuenta.setOnClickListener {
            copyText("Número de cuenta", numeroCuenta)
        }

        binding.btnCopyClabe.setOnClickListener {
            copyText("Cuenta CLABE", clabe)
        }

        binding.btnCopyTarjeta.setOnClickListener {
            copyText("Tarjeta de débito", tarjeta)
        }

        binding.btnCompartir.setOnClickListener {
            val texto = """
                Cuenta Efectiva Digital
                
                Número de cuenta: $numeroCuenta
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
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        clipboard.setPrimaryClip(
            ClipData.newPlainText(label, value)
        )

        Snackbar.make(binding.root, "$label copiado", Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}