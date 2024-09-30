package com.example.jetpacktest.data

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val type: Int,
    val state: Int,
    val giver_id: Int,
    val receiver_id: Int,
    val info: String? = null,
    val start_date: Instant,
    val duration_seconds: Int? = null,
    val interval_seconds: Int? = null,

    val taskDrugs: List<TaskDrug>? = null
) {
    val startDate = start_date.toLocalDateTime(TimeZone.currentSystemDefault())
    val typeId = Type.entries.find { type == it.id }

    enum class Type(val id: Int, val label: String) {
        INFO_TASK(501, "Information"),
        DRUG_TASK(502, "Drug prescription")
    }
}