@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.jetpacktest.util

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import com.example.jetpacktest.routes.Route
import com.example.jetpacktest.ui.screens.LoadingIndicator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val httpClient by lazy {
    HttpClient(Android) {
//        expectSuccess = true

        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
}

sealed class Response<out T> {
    // im not sure if this shouldnt be Any instead of Nothing
    data object Idle : Response<Nothing>()
    data object Loading : Response<Nothing>()
    // this is kinda irritating and i feel like maybe shouldnt exist
    data class Result<out T>(val result: T) : Response<T>()
    data class Error(val message: String = "Generic Error") : Response<Nothing>()
    val resultOrNull get() = (this as? Result<T>)?.result
    val messageOrNull get() = (this as? Error)?.message

    fun <X> cast(): Response<X> = when (this) {
        is Idle -> this
        is Loading -> this
        is Error -> this
        else -> throw kotlin.Error("cannot cast")
    }

    fun <X> cast(handler: (T) -> X): Response<X> = when (this) {
        is Result -> Result(handler(result))
        else -> cast()
    }

    companion object {
        fun <T> notNull(result: T?) = if (result != null) Response.Result(result) else Response.Error()
    }
}

suspend inline fun <reified T> HttpResponse.toResponse()
    = if (this.status.isSuccess())
        Response.Result(this.body<T>())
    else
        Response.Error("${this.status} ${this.body<String>()}")

fun <T> responseFlow(request: suspend () -> Response<T>) = flow {
    emit(Response.Loading)
    emit(request())
}

typealias ResponseFlow<T> = Flow<Response<T>>

suspend fun <T> ResponseFlow<T>.response(
    onLoading: suspend () -> Unit = { },
    onError: suspend (String) -> Unit = { },
    onResult: suspend (T) -> Unit = { },
) = this.map {
    if (it is Response.Loading) {
        onLoading()
    }

    if (it is Response.Error) {
        onError(it.message)
    }

    if (it is Response.Result) {
        onResult(it.result)
    }

    it
}

fun <T, X> ResponseFlow<T>.result(handler: suspend (T) -> X)
    = flatMapConcat { response ->
        flowOf(if (response is Response.Result) Response.Result(handler(response.result)) else response.cast())
    }

fun <T, X> ResponseFlow<T>.chain(handler: suspend (T) -> ResponseFlow<X>)
    = flatMapConcat { response ->
        if (response is Response.Result) handler(response.result) else flowOf(response.cast())
    }

fun <T> ResponseFlow<T>.mapResult()
    = filterIsInstance<Response.Result<T>>().map { it.result }

fun <T> ResponseFlow<T>.toState(scope: CoroutineScope)
    = stateIn(scope, SharingStarted.WhileSubscribed(5000), Response.Loading)

inline fun <reified T> Any?.instanceOf() = this is T

inline fun <reified T> HttpClient.getResponse(urlString: String, crossinline block: HttpRequestBuilder.() -> Unit = {})
    = responseFlow { get(urlString, block).toResponse<T>() }

inline fun <reified T> HttpClient.postResponse(urlString: String, crossinline block: HttpRequestBuilder.() -> Unit = {})
    = responseFlow { post(urlString, block).toResponse<T>() }

inline fun <reified T> HttpClient.putResponse(urlString: String, crossinline block: HttpRequestBuilder.() -> Unit = {})
    = responseFlow { put(urlString, block).toResponse<T>() }

inline fun <reified T> HttpClient.deleteResponse(urlString: String, crossinline block: HttpRequestBuilder.() -> Unit = {})
    = responseFlow { delete(urlString, block).toResponse<T>() }

@Composable
fun <T> rememberResponse(response: Response<T> = Response.Idle) = remember { mutableStateOf<Response<T>>(response) }

@Composable
fun rememberAnyResponse(response: Response<*> = Response.Idle) = remember { mutableStateOf(response) }

@Composable
fun <T> Request(
    responseFlow: ResponseFlow<T>,
    content: @Composable (Response<T>) -> Unit = { }
) {
    var response by rememberResponse<T>()

    LaunchedEffect(Unit) { responseFlow.collect { response = it } }
    content(response)
}

@Composable
fun <T> WaitRequest(
    responseFlow: ResponseFlow<T>,
    content: @Composable (T) -> Unit = { }
) {
    Request(responseFlow) { response ->
        when (response) {
            is Response.Result -> content(response.result)
            is Response.Error -> Text(response.message)
            else -> LoadingIndicator()
        }
    }
}

@Composable
fun <T> ListRequest(
    responseFlow: ResponseFlow<List<T>>,
    content: @Composable (T) -> Unit
) {
    WaitRequest(responseFlow) { list ->
        list.forEach { item -> content(item) }
    }
}

@Composable
fun <T> RequestButton(
    responseFlow: ResponseFlow<T>,
    collect: suspend (Response<T>) -> Unit = { },
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    var responseState by rememberAnyResponse()
    val loading = responseState is Response.Loading

    Button(
        {
            coroutineScope.launch {
                responseFlow.collect {
                    responseState = it

                    if (!it.instanceOf<Response.Loading>()) {
                        collect(it)
                    }
                }
            }
        },
        enabled = enabled && !loading
    ) {
        if (loading) {
            LoadingIndicator()
        } else {
            content()
        }
    }
}

@Composable
fun LoadingTextButton(
    onClick: () -> Unit,
    loading: Boolean = false,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    TextButton(onClick, enabled = enabled && !loading) {
        if (loading) {
            LoadingIndicator()
        } else {
            content()
        }
    }
}

fun NavHostController.goBack() {
    if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED)
        popBackStack()
}

fun NavHostController.navigate(route: Route, vararg arguments: Any) {
    navigate(route.replace(*arguments).route) { launchSingleTop = true }
}