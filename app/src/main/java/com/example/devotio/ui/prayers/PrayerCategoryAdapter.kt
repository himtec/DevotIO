package com.example.devotio.ui.prayers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devotio.R
import com.example.devotio.data.PrayerCategory
import com.example.devotio.databinding.ItemPrayerCategoryBinding

class PrayerCategoryAdapter(
    private val onCategoryClick: (PrayerCategory) -> Unit
) : ListAdapter<PrayerCategory, PrayerCategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemPrayerCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        try {
            holder.bind(getItem(position))
        } catch (e: Exception) {
            // Log error or handle it appropriately
            e.printStackTrace()
        }
    }

    inner class CategoryViewHolder(
        private val binding: ItemPrayerCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    try {
                        onCategoryClick(getItem(position))
                    } catch (e: Exception) {
                        // Log error or handle it appropriately
                        e.printStackTrace()
                    }
                }
            }
        }

        fun bind(category: PrayerCategory) {
            binding.apply {
                categoryIcon.setImageResource(category.iconResId)
                categoryName.text = category.name
                categoryIcon.contentDescription = "Category icon for ${category.name}"
            }
        }
    }

    private class CategoryDiffCallback : DiffUtil.ItemCallback<PrayerCategory>() {
        override fun areItemsTheSame(oldItem: PrayerCategory, newItem: PrayerCategory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PrayerCategory, newItem: PrayerCategory): Boolean {
            return oldItem == newItem
        }
    }
} 