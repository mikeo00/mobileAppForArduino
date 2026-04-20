package com.example.daizcode.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daizcode.data.model.DashboardState
import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.data.repository.FakeDetectionRepository
import kotlinx.coroutines.delay
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
 * Collects events from the fake repository and maintains the dashboard state.
 */
class DashboardViewModel : ViewModel() {

    private val repository = FakeDetectionRepository()

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    // Track defects in current minute window for the line chart
    private var defectsInCurrentWindow = 0
    private val maxChartPoints = 20 // Keep last 20 data points on chart

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        startCollecting()
        startDefectsPerMinuteTracker()
    }

    /**
     * Collects detection events and updates state in real-time.
     */
    private fun startCollecting() {
        viewModelScope.launch {
            repository.getDetectionStream().collect { event ->
                processEvent(event)
            }
        }
    }

    /**
     * Processes a single detection event and updates the dashboard state.
     */
    private fun processEvent(event: DetectionEvent) {
        if (event.status == DetectionStatus.DEFECTIVE) {
            defectsInCurrentWindow++
        }

        _state.update { current ->
            val newTotal = current.totalProcessed + 1
            val newGood = if (event.status == DetectionStatus.GOOD) current.goodCount + 1 else current.goodCount
            val newDefective = if (event.status == DetectionStatus.DEFECTIVE) current.defectiveCount + 1 else current.defectiveCount

            // Keep last 50 recent events
            val updatedEvents = listOf(event) + current.recentEvents.take(49)

            current.copy(
                totalProcessed = newTotal,
                goodCount = newGood,
                defectiveCount = newDefective,
                recentEvents = updatedEvents
            )
        }
    }

    /**
     * Every 10 seconds, snapshot the defect count and push to the chart data.
     * This simulates "defects per minute" as a rolling window.
     */
    private fun startDefectsPerMinuteTracker() {
        viewModelScope.launch {
            while (true) {
                delay(10_000L) // Sample every 10 seconds for visible chart updates
                val defectCount = defectsInCurrentWindow.toFloat()
                defectsInCurrentWindow = 0

                _state.update { current ->
                    val updatedDefects = (current.defectsPerMinute + defectCount).takeLast(maxChartPoints)
                    val updatedLabels = (current.timeLabels + timeFormat.format(Date())).takeLast(maxChartPoints)

                    current.copy(
                        defectsPerMinute = updatedDefects,
                        timeLabels = updatedLabels
                    )
                }
            }
        }
    }
}
