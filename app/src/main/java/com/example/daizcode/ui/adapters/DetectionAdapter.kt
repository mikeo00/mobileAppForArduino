package com.example.daizcode.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.daizcode.R
import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.databinding.ItemDetectionEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetectionAdapter : ListAdapter<DetectionEvent, DetectionAdapter.ViewHolder>(DiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetectionEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemDetectionEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: DetectionEvent) {
            val isDefective = event.status == DetectionStatus.DEFECTIVE
            val context = binding.root.context

            val statusColor = if (isDefective) {
                ContextCompat.getColor(context, R.color.accent_red)
            } else {
                ContextCompat.getColor(context, R.color.accent_green)
            }

            val glowColor = if (isDefective) {
                ContextCompat.getColor(context, R.color.accent_red_glow)
            } else {
                ContextCompat.getColor(context, R.color.accent_green_glow)
            }

            binding.statusDot.setBackgroundColor(statusColor)
            binding.statusGlow.setBackgroundColor(glowColor)
            
            binding.eventTitle.text = "${if (isDefective) "Defect" else "Good"} ${event.objectType.displayName}"
            binding.eventTime.text = timeFormat.format(Date(event.timestamp))
            
            binding.confidenceValue.text = "%.0f%%".format(event.confidence * 100)
            binding.confidenceValue.setTextColor(statusColor)
            
            // Update confidence badge background alpha
            val badgeBgColor = if (isDefective) {
                ContextCompat.getColor(context, R.color.accent_red)
            } else {
                ContextCompat.getColor(context, R.color.accent_green)
            }
            // In a real app we might use a dynamic drawable or ColorUtils to set alpha
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DetectionEvent>() {
        override fun areItemsTheSame(oldItem: DetectionEvent, newItem: DetectionEvent) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DetectionEvent, newItem: DetectionEvent) =
            oldItem == newItem
    }
}
