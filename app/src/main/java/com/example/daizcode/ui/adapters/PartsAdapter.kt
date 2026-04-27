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
import com.example.daizcode.databinding.ItemPartEntryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adapter for the Parts screen. Displays detection events with type, status,
 * confidence, time, and date.
 */
class PartsAdapter : ListAdapter<DetectionEvent, PartsAdapter.ViewHolder>(DiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPartEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPartEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: DetectionEvent) {
            val context = binding.root.context
            val isDefective = event.status == DetectionStatus.DEFECTIVE

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

            // Status indicator
            binding.partStatusDot.setBackgroundColor(statusColor)
            binding.partStatusGlow.setBackgroundColor(glowColor)

            // Type
            binding.partType.text = event.objectType.displayName

            // Status label
            binding.partStatus.text = if (isDefective) "DEFECTIVE" else "GOOD"
            binding.partStatus.setTextColor(statusColor)

            // Confidence
            binding.partConfidence.text = "%.0f%%".format(event.confidence * 100)

            // Time and Date
            val date = Date(event.timestamp)
            binding.partTime.text = timeFormat.format(date)
            binding.partDate.text = dateFormat.format(date)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DetectionEvent>() {
        override fun areItemsTheSame(oldItem: DetectionEvent, newItem: DetectionEvent) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DetectionEvent, newItem: DetectionEvent) =
            oldItem == newItem
    }
}
