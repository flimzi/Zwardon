package com.example.jetpacktest.data

import com.example.jetpacktest.R
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
data class Task(
    val id: Int,
    val type: Int,
    val status: Int,
    val giver_id: Int,
    val receiver_id: Int,
    val info: String? = null,
    val start_date: Instant,
    val duration_seconds: Int? = null,
    val interval_seconds: Int? = null,
) {
    val startDate get() = start_date.toLocalDateTime(TimeZone.currentSystemDefault())

    val typeId get() = when (type) {
        501 -> R.string.task501
        else -> R.string.task500
    }
}