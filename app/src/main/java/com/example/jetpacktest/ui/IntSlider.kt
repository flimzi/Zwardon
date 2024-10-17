package com.example.jetpacktest.ui

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable

@Composable
fun IntSlider(range: IntRange, value: Int? = null, onChange: (Int) -> Unit = { }) {
    Slider(
        value?.coerceIn(range)?.toFloat() ?: range.min().toFloat(),
        { onChange(it.toInt()) },
        valueRange = range.first.toFloat()..range.last.toFloat(),
        steps = range.count()
    )
}