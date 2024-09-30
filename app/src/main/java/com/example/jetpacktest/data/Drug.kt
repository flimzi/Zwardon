package com.example.jetpacktest.data

import kotlinx.serialization.Serializable

@Serializable
data class Drug(
    val id: Int = 0,
    val userId: Int = 0,
    val category: Int = 0,
    val name: String = "",
    val unit: Int = 0,
    val info: String? = null,
) {
    val unitId = Unit.entries.find { it.id == unit }!!
    val categoryId = Category.entries.find { it.id == category }!!

    enum class Category(val id: Int, val label: String) {
        NONE(1, "No Category"),
        PAIN_MANAGEMENT(2, "Pain Management")
    }
}

@Serializable
data class TaskDrug(
    val taskId: Int = 0,
    val drugId: Int = 0,
    val amount: Int = 0
)

enum class Unit(val id: Int, val short: String) {
    MILLILITER(1, "mL"),
    MILLIGRAM(2, "mg"),
}