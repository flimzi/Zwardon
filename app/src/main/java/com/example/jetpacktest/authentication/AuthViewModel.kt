package com.example.jetpacktest.authentication

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.extensions.dataStore
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AuthViewModel(application: Application) : ViewModel() {
    private val dataStore = application.applicationContext.dataStore

    val userState = dataStore.data
        .map { it[JWT_TOKEN] }
        .map { it?.let { Api.Users.get(it).body<User>().copy(accessToken = it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val user: User get() = userState.value!!
    val accessToken: String get() = user.accessToken!!

    suspend fun login(accessToken: String) {
        dataStore.edit { it[JWT_TOKEN] = accessToken }
    }

    suspend fun login(email: String, password: String): Boolean
        = Api.Authentication.login(email, password).let {
            if (it.status != HttpStatusCode.OK)
                return false

            login(it.body<String>())
            return true
        }

    suspend fun logout() {
        dataStore.edit { it.remove(JWT_TOKEN) }
    }

    companion object {
        val JWT_TOKEN = stringPreferencesKey("jwt_token")

        val Factory = viewModelFactory {
            initializer {
                AuthViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}
