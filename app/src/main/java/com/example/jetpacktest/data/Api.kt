package com.example.jetpacktest.data

import com.example.jetpacktest.util.httpClient
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

object Api {
    private const val BASE = "http://10.0.2.2:3000"
    private const val API = "$BASE/api"

    object Authentication {
        private const val AUTH = "$API/auth"
        private const val LOGIN = "$AUTH/login"
        private const val LOGOUT = "$AUTH/logout"
        private const val VERIFY = "$AUTH/verfiy"

        suspend fun login(email: String, password: String) =
            httpClient.post(LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }

        suspend fun logout(accessToken: String) =
            httpClient.get(LOGOUT) { bearerAuth(accessToken) }

        suspend fun verify(verificationToken: String) =
            httpClient.post(VERIFY) { setBody(verificationToken) }
    }

    object Users {
        private const val USERS = "$API/users"
        fun id(userId: Int? = null) = "$API/user/${userId ?: "current"}"

        suspend fun add(user: User) =
            httpClient.post(USERS) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        suspend fun add(accessToken: String, user: User) =
            httpClient.post(USERS) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        suspend fun get(accessToken: String, userId: Int? = null) =
            httpClient.get(id(userId)) { bearerAuth(accessToken) }

        suspend fun update(accessToken: String, userId: Int? = null, user: User) =
            httpClient.get(id(userId)) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        suspend fun delete(accessToken: String, userId: Int? = null) =
            httpClient.delete(id(userId)) { bearerAuth(accessToken) }

        suspend fun token(accessToken: String, userId: Int? = null) =
            httpClient.get(id(userId) + "/token") { bearerAuth(accessToken) }

        suspend fun logoutAll(accessToken: String, userId: Int? = null) =
            httpClient.get(id(userId) + "/logoutAll") { bearerAuth(accessToken) }

        suspend fun getSecondary(accessToken: String, userId: Int? = null) =
            httpClient.get(id(userId) + "/related/children") { bearerAuth(accessToken) }
    }

    object Events {
        fun id(userId: Int?, eventId: Int) = Users.id(userId) + "/event/$eventId"
        fun upcomingTasks(userId: Int? = null) = Users.id(userId) + "/tasks/upcoming"

        suspend fun add(accessToken: String, userId: Int? = null, event: Any) =
            httpClient.post(Users.id(userId) + "/events") {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(event)
            }

        suspend fun get(accessToken: String, userId: Int? = null, eventId: Int) =
            httpClient.get(id(userId, eventId)) { bearerAuth(accessToken) }

        suspend fun update(accessToken: String, userId: Int? = null, eventId: Int, event: Any) =
            httpClient.put(id(userId, eventId)) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(event)
            }

        suspend fun delete(accessToken: String, userId: Int? = null, eventId: Int) =
            httpClient.delete(id(userId, eventId)) { bearerAuth(accessToken) }

        suspend fun getUpcomingTasks(accessToken: String, userId: Int? = null) =
            httpClient.get(upcomingTasks(userId)) { bearerAuth(accessToken) }
    }

    object Fcm {
        private const val FCM = "$API/fcm"
        private const val TOKEN = "$FCM/token"

        suspend fun token(accessToken: String, fcmToken: String) =
            httpClient.put(TOKEN) {
                bearerAuth(accessToken)
                contentType(ContentType.Text.Plain)
                setBody(fcmToken)
            }
    }
}