package com.example.jetpacktest.data

import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.util.ResponseFlow
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

interface Model<T> {
    val id: Int
    fun load(accessToken: String): ResponseFlow<T>
}

@Serializable
data class User(
    override val id: Int = -1,
    val role: Int = Role.SECONDARY.id,
    val email: String? = null,
    val password: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val birth_date: Instant? = null,
): Model<User> {
    @Transient val fullName = "$first_name $last_name"
    @Transient val isReal = id >= 0
    @Transient val roleId = Role.entries.find { it.id == role }!!

    override fun load(accessToken: String) = Api.Users.get(accessToken, id)

    enum class Role(val id: Int) {
        PRIMARY(1),
        SECONDARY(2)
    }
}

data class AuthenticatedUser(val details: User, val accessToken: String) {
    val id = details.id
    val children = Api.Users.getChildren(accessToken, id)
}