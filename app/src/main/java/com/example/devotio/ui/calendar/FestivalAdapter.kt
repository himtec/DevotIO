package com.example.devotio.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devotio.databinding.ItemFestivalBinding

class FestivalAdapter : ListAdapter<Festival, FestivalAdapter.FestivalViewHolder>(FestivalDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val binding = ItemFestivalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FestivalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FestivalViewHolder(
        private val binding: ItemFestivalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(festival: Festival) {
            binding.apply {
                festivalName.text = festival.name
                festivalDate.text = festival.date
                festivalDescription.text = festival.description
            }
        }
    }

    private class FestivalDiffCallback : DiffUtil.ItemCallback<Festival>() {
        override fun areItemsTheSame(oldItem: Festival, newItem: Festival): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Festival, newItem: Festival): Boolean {
            return oldItem == newItem
        }
    }
} 