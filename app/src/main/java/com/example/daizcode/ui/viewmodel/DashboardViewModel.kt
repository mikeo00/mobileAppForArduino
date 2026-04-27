package com.example.daizcode.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daizcode.data.model.DashboardState
import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.data.repository.EspDetectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for the dashboard screen.
 * Fetches detections from the ESP32 and updates the dashboard state.
 * Each fetch reads ALL JSON lines the ESP sends in one connection.
 */
class DashboardViewModel : ViewModel() {

    private val repository = EspDetectionRepository()

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        fetchFromEsp()
    }

    /**
     * Connects to the ESP32 once, fetches ALL detection results it sends,
     * and updates the dashboard state with each one. Does NOT loop.
     * Can be called again (e.g. from a button) to trigger a new scan.
     */
    fun fetchFromEsp() {
        viewModelScope.launch {
            val events = repository.fetchDetections()
            for (event in events) {
                processEvent(event)
            }
        }
    }

    /**
     * Processes a single detection event and updates the dashboard state.
     */
    private fun processEvent(event: DetectionEvent) {
        _state.update { current ->
            val newTotal = current.totalProcessed + 1
            val newGood = if (event.status == DetectionStatus.GOOD) current.goodCount + 1 else current.goodCount
            val newDefective = if (event.status == DetectionStatus.DEFECTIVE) current.defectiveCount + 1 else current.defectiveCount

            // Keep last 50 recent events
            val updatedEvents = listOf(event) + current.recentEvents.take(49)

            // Add a chart data point for this detection
            val defectValue = if (event.status == DetectionStatus.DEFECTIVE) 1f else 0f
            val updatedDefects = (current.defectsPerMinute + defectValue).takeLast(20)
            val updatedLabels = (current.timeLabels + timeFormat.format(Date())).takeLast(20)

            current.copy(
                totalProcessed = newTotal,
                goodCount = newGood,
                defectiveCount = newDefective,
                recentEvents = updatedEvents,
                defectsPerMinute = updatedDefects,
                timeLabels = updatedLabels
            )
        }
    }
}
