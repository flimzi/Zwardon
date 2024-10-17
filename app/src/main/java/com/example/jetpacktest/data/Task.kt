package com.example.jetpacktest.data

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int = -1,
    val type: Int = Type.DRUG_TASK.id,
    val state: Int = -1,
    val giver_id: Int = -1,
    val receiver_id: Int = -1,
    val info: String? = null,
    val start_date: Instant = Clock.System.now(),
    val duration_seconds: Int? = null,
    val interval_seconds: Int? = null,

    val taskDrugs: List<TaskDrug>? = null
) {
    val startDate = start_date.toLocalDateTime(TimeZone.currentSystemDefault())
    val typeId = Type.entries.find { type == it.id }!!
    val durationOrDefault = duration_seconds ?: 5
    val recurring = interval_seconds != null

    enum class Type(val id: Int, val label: String) {
        INFO_TASK(501, "Information"),
        DRUG_TASK(502, "Drug prescription")
    }

    fun withStartDate(date: Instant, hours: Int = 0, minutes: Int = 0): Task {
        val localDate = date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val localTime = LocalTime(hours, minutes)
        return this.copy(start_date = LocalDateTime(localDate, localTime).toInstant(TimeZone.currentSystemDefault()))
    }
}