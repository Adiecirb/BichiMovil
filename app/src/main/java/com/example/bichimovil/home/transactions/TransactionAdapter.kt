package com.example.bichimovil.home.transactions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter del historial de movimientos (GET /transaction).
 * Usa el campo "direction" que devuelve la API:
 *  - "out" → salida (rojo, signo -)
 *  - "in"  → entrada (verde, signo +)
 */
class TransactionAdapter :
    ListAdapter<TransactionResponse, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))

        fun bind(item: TransactionResponse) {
            val isOut = item.direction == "out"

            binding.tvConcepto.text = when {
                !item.description.isNullOrBlank() -> item.description
                isOut -> "Transferencia enviada"
                else -> "Transferencia recibida"
            }

            binding.tvFecha.text = dateFormat.format(item.date.toDate())

            val sign = if (isOut) "-" else "+"
            binding.tvMonto.text = "$sign${item.amount.toCurrencyMXN()}"
            binding.tvMonto.setTextColor(
                if (isOut) Color.parseColor("#C62828") else Color.parseColor("#2E7D32")
            )

            binding.tvArrow.text = if (isOut) "↑" else "↓"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TransactionResponse>() {
        override fun areItemsTheSame(old: TransactionResponse, new: TransactionResponse) =
            old.id == new.id

        override fun areContentsTheSame(old: TransactionResponse, new: TransactionResponse) =
            old == new
    }
}
