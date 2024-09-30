package com.example.jetpacktest.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.jetpacktest.data.AuthenticatedUser
import com.example.jetpacktest.data.User
import com.example.jetpacktest.routes.Api
import com.example.jetpacktest.util.Response
import com.example.jetpacktest.util.chain
import com.example.jetpacktest.util.result
import com.example.jetpacktest.util.toState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map

class AuthRepository(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Unconfined)

    val userState = context.authStore.data
        .map { Response.notNull(it[tokenKey]) }
        .chain { token -> Api.Users.get(token).result { user -> AuthenticatedUser(user, token) } }
        .toState(scope)

    suspend fun login(accessToken: String) {
        context.authStore.edit { it[tokenKey] = accessToken }
    }

    fun login(email: String, password: String)
        = Api.Authentication.login(email, password).result { login(it) }

    suspend fun logout() {
        context.authStore.edit { it.remove(tokenKey) }
    }

    fun register(email: String, password: String)
        = Api.Users.add(User(role = 1, email = email, password = password)).chain { login(email, password) }

    companion object {
        val tokenKey = stringPreferencesKey("jwt_token")
        val Context.authStore by preferencesDataStore("auth")
    }
}