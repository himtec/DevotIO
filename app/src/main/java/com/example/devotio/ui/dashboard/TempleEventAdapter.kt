package com.example.devotio.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devotio.data.TempleEvent
import com.example.devotio.databinding.ItemTempleEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TempleEventAdapter : ListAdapter<TempleEvent, TempleEventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemTempleEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemTempleEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        fun bind(event: TempleEvent) {
            binding.apply {
                eventName.text = event.name
                eventDescription.text = event.description
                eventDate.text = dateFormat.format(event.date)
                eventTime.text = "${event.startTime} - ${event.endTime}"
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<TempleEvent>() {
        override fun areItemsTheSame(oldItem: TempleEvent, newItem: TempleEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TempleEvent, newItem: TempleEvent): Boolean {
            return oldItem == newItem
        }
    }
} 