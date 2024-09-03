package com.example.jetpacktest.authentication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.jetpacktest.data.Api
import com.example.jetpacktest.data.User
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.apache.commons.lang3.mutable.Mutable

private val Context.dataStore by preferencesDataStore(name = "settings")

class AuthViewModel(application: Application) : ViewModel() {
    private val dataStore = application.applicationContext.dataStore
    val accessTokenState = MutableStateFlow<Response<String?>>(Response.Loading)
    val accessTokenData = accessTokenState.asLiveData()
    val accessToken = MutableStateFlow<String?>(null)

    val userState = MutableStateFlow<Response<User?>>(Response.Loading)
    val user = MutableStateFlow<User?>(null)

    init {
        accessTokenData.observeForever { tokenResponse ->
            if (tokenResponse is Response.Result<String?>) {
                if (tokenResponse.data != null) {
                    viewModelScope.launch {
                        userState.value = try {
                            user.value = Api.Users.get(tokenResponse.data).body()
                            Response.Result(user.value)
                        } catch (e : Exception) {
                            dataStore.edit { it.remove(JWT_TOKEN_KEY) }
                            Response.Result(null)
                        }
                    }
                } else {
                    userState.value = Response.Result(null)
                }
            }
        }

        viewModelScope.launch {
            try {
                dataStore.data.map { it[JWT_TOKEN_KEY] }.collect {
                    accessToken.value = it
                    accessTokenState.value = Response.Result(it)
                }
            } catch (e : Exception) {
                dataStore.edit { it.remove(JWT_TOKEN_KEY) }
                accessTokenState.value = Response.Result(null)
            }
        }
    }

    // there also needs to be a method of generating, displaying and scanning the qr code for a patient
    suspend fun login(email: String, password: String): Boolean {
        val response = Api.Authentication.login(email, password)

        if (response.status != HttpStatusCode.OK)
            return false

        val token = response.body<String>()
        dataStore.edit { it[JWT_TOKEN_KEY] = token }
        accessTokenState.value = Response.Result(token)
        return true
    }

    suspend fun logout() {
        (accessTokenState.value as? Response.Result<String?>)?.data?.let {
            Api.Authentication.logout(it)
        }

        dataStore.edit { it.remove(JWT_TOKEN_KEY) }
    }

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")

        val Factory = viewModelFactory {
            initializer {
                AuthViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }
}