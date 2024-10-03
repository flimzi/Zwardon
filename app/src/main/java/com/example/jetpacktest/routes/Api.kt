package com.example.jetpacktest.routes

import com.example.jetpacktest.data.Drug
import com.example.jetpacktest.data.User
import com.example.jetpacktest.util.chain
import com.example.jetpacktest.util.deleteResponse
import com.example.jetpacktest.util.getResponse
import com.example.jetpacktest.util.httpClient
import com.example.jetpacktest.util.postResponse
import com.example.jetpacktest.util.putResponse
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
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

        fun login(email: String, password: String) =
            httpClient.postResponse<String>(LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }

        fun logout(accessToken: String)
            = httpClient.getResponse<String>(LOGOUT) { bearerAuth(accessToken) }

        fun verify(verificationToken: String)
            = httpClient.postResponse<String>(VERIFY) { setBody(verificationToken) }
    }

    object Users {
        private const val USERS = "$API/users"
        fun id(userId: Int? = null) = "$API/user/${userId ?: "current"}"

        fun add(user: User)
            = httpClient.postResponse<Int>(USERS) {
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        fun add(accessToken: String, user: User)
            = httpClient.postResponse<Int>(USERS) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        fun get(accessToken: String, userId: Int? = null)
            = httpClient.getResponse<User>(id(userId)) { bearerAuth(accessToken) }

        // not sure why this has to be suspend
        fun addGet(accessToken: String, user: User)
            = add(accessToken, user).chain { get(accessToken, it) }

        fun update(accessToken: String, userId: Int? = null, user: User)
            = httpClient.putResponse<String>(id(userId)) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(user)
            }

        fun delete(accessToken: String, userId: Int? = null)
            = httpClient.deleteResponse<String>(id(userId)) { bearerAuth(accessToken) }

        fun token(accessToken: String, userId: Int? = null)
            = httpClient.getResponse<String>(id(userId) + "/token") { bearerAuth(accessToken) }

        fun logoutAll(accessToken: String, userId: Int? = null) =
            httpClient.getResponse<String>(id(userId) + "/logoutAll") { bearerAuth(accessToken) }

        fun getChildren(accessToken: String, userId: Int? = null) =
            httpClient.getResponse<List<User>>(id(userId) + "/related/children") { bearerAuth(accessToken) }
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

    object Drugs {
        private fun drug(userId: Int?, drugId: Int) = Users.id(userId) + "/drug/$drugId"
        private fun drugs(userId: Int?) = Users.id(userId) + "/drugs"

        fun get(accessToken: String, userId: Int?) =
            httpClient.getResponse<Drug>(drugs(userId)) {
                bearerAuth(accessToken)
            }

        fun get(accessToken: String, userId: Int? = null, drugId: Int) =
            httpClient.getResponse<Drug>(drug(userId, drugId)) {
                bearerAuth(accessToken)
            }

        fun add(accessToken: String, userId: Int, drug: Drug) =
            httpClient.postResponse<Int>(drugs(userId)) {
                bearerAuth(accessToken)
                contentType(ContentType.Application.Json)
                setBody(drug)
            }
    }
}