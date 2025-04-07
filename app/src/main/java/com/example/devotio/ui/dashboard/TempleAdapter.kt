package com.example.devotio.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.devotio.R
import com.example.devotio.data.Temple
import com.example.devotio.databinding.ItemTempleBinding

class TempleAdapter(
    private val onTempleClick: (Temple) -> Unit
) : ListAdapter<Temple, TempleAdapter.TempleViewHolder>(TempleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempleViewHolder {
        val binding = ItemTempleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TempleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TempleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TempleViewHolder(
        private val binding: ItemTempleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTempleClick(getItem(position))
                }
            }
        }

        fun bind(temple: Temple) {
            binding.apply {
                templeName.text = temple.name
                templeAddress.text = temple.address
                templeTiming.text = "${temple.openingTime} - ${temple.closingTime}"

                // Load temple image using Glide
                temple.imageUrl?.let { url ->
                    Glide.with(templeImage)
                        .load(url)
                        .placeholder(R.drawable.ic_temple_black_24dp)
                        .error(R.drawable.ic_temple_black_24dp)
                        .centerCrop()
                        .into(templeImage)
                }
            }
        }
    }

    private class TempleDiffCallback : DiffUtil.ItemCallback<Temple>() {
        override fun areItemsTheSame(oldItem: Temple, newItem: Temple): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Temple, newItem: Temple): Boolean {
            return oldItem == newItem
        }
    }
} 