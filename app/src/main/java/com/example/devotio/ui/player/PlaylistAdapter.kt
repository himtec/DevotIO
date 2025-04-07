package com.example.devotio.ui.player

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devotio.data.Prayer
import com.example.devotio.databinding.ItemPlaylistBinding

class PlaylistAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<Prayer, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    private var currentPlayingIndex: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = ItemPlaylistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    fun setCurrentPlayingIndex(index: Int) {
        val oldIndex = currentPlayingIndex
        currentPlayingIndex = index
        if (oldIndex != -1) notifyItemChanged(oldIndex)
        if (index != -1) notifyItemChanged(index)
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(position)
                }
            }
        }

        fun bind(prayer: Prayer, position: Int) {
            binding.apply {
                itemNumber.text = (position + 1).toString()
                prayerTitle.text = prayer.title
                prayerDescription.text = prayer.description
                currentlyPlaying.visibility = if (position == currentPlayingIndex) View.VISIBLE else View.GONE
            }
        }
    }

    private class PlaylistDiffCallback : DiffUtil.ItemCallback<Prayer>() {
        override fun areItemsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Prayer, newItem: Prayer): Boolean {
            return oldItem == newItem
        }
    }
} 