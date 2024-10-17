package com.example.jetpacktest.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Drug(
    val id: Int = -1,
    val userId: Int = -1,
    val category: Int = Category.NONE.id,
    val name: String = "",
    val unit: Int = AmountUnit.MILLIGRAM.id,
    val info: String? = null,
) {
    @Transient val unitId = AmountUnit.entries.find { it.id == unit }!!
    @Transient val categoryId = Category.entries.find { it.id == category }!!

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

enum class AmountUnit(val id: Int, val short: String) {
    MILLILITER(1, "mL"),
    MILLIGRAM(2, "mg"),
}