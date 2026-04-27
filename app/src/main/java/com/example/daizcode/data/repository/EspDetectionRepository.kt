package com.example.daizcode.data.repository

import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.data.model.ObjectType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

/**
 * Repository that connects to the ESP32-CAM via a Socket connection.
 * Sends "ready", then reads ALL JSON lines the ESP sends before closing the connection.
 */
class EspDetectionRepository {

    private val ipAddress = "192.168.4.1"
    private val port = 1234

    /**
     * Connects to the ESP32, sends "ready", reads ALL JSON lines until the
     * ESP closes the connection, and returns them as a list of DetectionEvents.
     */
    suspend fun fetchDetections(): List<DetectionEvent> = withContext(Dispatchers.IO) {
        val results = mutableListOf<DetectionEvent>()
        var socket: Socket? = null
        try {
            socket = Socket(ipAddress, port)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            // Tell ESP32 we are ready
            writer.println("ready")

            // Read ALL lines until the ESP closes the connection (readLine returns null)
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val trimmed = line!!.trim()
                if (trimmed.startsWith("{")) {
                    try {
                        val jsonObject = JSONObject(trimmed)
                        results.add(parseJsonToEvent(jsonObject))
                    } catch (e: Exception) {
                        println("JSON Parsing error: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("Socket error: ${e.message}")
        } finally {
            try { socket?.close() } catch (_: Exception) {}
        }
        results
    }

    private fun parseJsonToEvent(json: JSONObject): DetectionEvent {
        val typeStr = json.optString("type", "nut")
        val statusStr = json.optString("status", "bad")
        val confidence = json.optDouble("confidence", 0.0).toFloat()

        return DetectionEvent(
            objectType = when (typeStr.lowercase()) {
                "nut" -> ObjectType.NUT
                "gear" -> ObjectType.GEAR
                "screw" -> ObjectType.SCREW
                else -> ObjectType.BOLT
            },
            status = if (statusStr.lowercase() == "bad" || statusStr.lowercase() == "defective") {
                DetectionStatus.DEFECTIVE
            } else {
                DetectionStatus.GOOD
            },
            confidence = confidence
        )
    }
}
