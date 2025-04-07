package com.example.devotio.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devotio.R
import com.example.devotio.data.Prayer
import com.example.devotio.databinding.ItemFavoritePrayerBinding

class FavoritePrayerAdapter(
    private val onPrayerClick: (Prayer) -> Unit,
    private val onRemoveClick: (Prayer) -> Unit
) : ListAdapter<Prayer, FavoritePrayerAdapter.PrayerViewHolder>(PrayerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val binding = ItemFavoritePrayerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            // Log error or handle it appropriately
            e.printStackTrace()
        }
    }

    inner class PrayerViewHolder(
        private val binding: ItemFavoritePrayerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onPrayerClick(getItem(position))
                    } catch (e: Exception) {
                        // Log error or handle it appropriately
                        e.printStackTrace()
                    }
                }
            }

            binding.removeButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onRemoveClick(getItem(position))
                    } catch (e: Exception) {
                        // Log error or handle it appropriately
                        e.printStackTrace()
                    }
                }
            }
        }

        fun bind(prayer: Prayer) {
            binding.apply {
                prayerTitle.text = prayer.title
                prayerCategory.text = prayer.description
                removeButton.setImageResource(R.drawable.ic_favorite_filled)
                removeButton.contentDescription = "Remove from favorites"
            }
        }
    }

    private class PrayerDiffCallback : DiffUtil.ItemCallback<Prayer>() {
        override fun areItemsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem == newItem
        }
    }
} 