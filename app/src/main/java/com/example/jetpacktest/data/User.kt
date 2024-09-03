package com.example.jetpacktest.data

import com.example.jetpacktest.util.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    val id: Int,
    val role: Int,
    val email: String? = null,
    val password: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class) val birth_date: LocalDateTime? = null
//    val accessToken: String? = null,
) {

}