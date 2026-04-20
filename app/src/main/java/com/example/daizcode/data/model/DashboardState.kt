package com.example.daizcode.data.model

/**
 * Holds the entire dashboard UI state.
 */
data class DashboardState(
    val totalProcessed: Int = 0,
    val goodCount: Int = 0,
    val defectiveCount: Int = 0,
    val recentEvents: List<DetectionEvent> = emptyList(),
    val defectsPerMinute: List<Float> = emptyList(),
    val timeLabels: List<String> = emptyList()
)
