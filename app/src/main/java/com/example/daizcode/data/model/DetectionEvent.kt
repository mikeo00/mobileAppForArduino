package com.example.daizcode.data.model

import java.util.UUID

/**
 * Represents a single detection event from the AI vision system.
 */
data class DetectionEvent(
    val id: String = UUID.randomUUID().toString(),
    val objectType: ObjectType,
    val status: DetectionStatus,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)

enum class DetectionStatus {
    GOOD, DEFECTIVE
}

enum class ObjectType(val displayName: String) {
    BOLT("Bolt"),
    GEAR("Gear"),
    SCREW("Screw")
}
