package com.example.bichimovil.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.databinding.ItemBeneficiaryBinding

class BeneficiaryAdapter :
    ListAdapter<BeneficiaryResponse, BeneficiaryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBeneficiaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemBeneficiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BeneficiaryResponse) {
            binding.apply {
                tvAlias.text = item.alias
                tvNombreCompleto.text = "${item.name} ${item.lastName}"
                tvCuenta.text = "*${item.accountNumber.takeLast(4)}"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BeneficiaryResponse>() {
        override fun areItemsTheSame(oldItem: BeneficiaryResponse, newItem: BeneficiaryResponse) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: BeneficiaryResponse, newItem: BeneficiaryResponse) =
            oldItem == newItem
    }
}