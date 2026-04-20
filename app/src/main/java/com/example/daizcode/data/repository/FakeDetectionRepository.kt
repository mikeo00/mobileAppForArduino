package com.example.daizcode.data.repository

import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.data.model.ObjectType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

/**
 * Simulates a real-time detection data source.
 * Emits a new DetectionEvent every 1–2 seconds with randomized properties.
 * Defective probability is ~20%.
 */
class FakeDetectionRepository {

    private val objectTypes = ObjectType.entries.toTypedArray()

    /**
     * Returns an infinite Flow that emits detection events at random intervals.
     */
    fun getDetectionStream(): Flow<DetectionEvent> = flow {
        while (true) {
            val delayMs = Random.nextLong(1000L, 2001L) // 1–2 seconds
            delay(delayMs)

            val event = DetectionEvent(
                objectType = objectTypes.random(),
                status = if (Random.nextFloat() < 0.20f) DetectionStatus.DEFECTIVE else DetectionStatus.GOOD,
                confidence = Random.nextFloat() * 0.29f + 0.70f // 0.70–0.99
            )
            emit(event)
        }
    }
}
