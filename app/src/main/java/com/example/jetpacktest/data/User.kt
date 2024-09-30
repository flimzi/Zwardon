package com.example.jetpacktest.data

import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.util.ResponseFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface Model<T> {
    val id: Int
    fun load(accessToken: String): ResponseFlow<T>
}

@Serializable
data class User(
    override val id: Int = -1,
    val role: Int = 0,
    val email: String? = null,
    val password: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val birth_date: Instant? = null,
): Model<User> {
    val fullName = "$first_name $last_name"
    val isReal = id >= 0

    override fun load(accessToken: String) = Api.Users.get(accessToken, id)
}

data class AuthenticatedUser(val details: User, val accessToken: String)