package com.example.jetpacktest.extensions

import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

suspend fun Call.await(): Result<Response> {
    return suspendCancellableCoroutine {
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (it.isActive)
                    it.resume(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                if (it.isActive)
                    it.resume(Result.success(response))
            }
        })
    }
}

fun Request.Builder.addBearer(token: String?): Request.Builder {
    token?.let { addHeader("Authorization", "Bearer $it") }
    return this
}

fun Request.Builder.call(client: OkHttpClient): Call {
    return client.newCall(build())
}

fun getJsonBody(body: Any) : RequestBody {
    return Gson().toJson(body).toRequestBody("application/json".toMediaType())
}

fun OkHttpClient.postJson(url: String, body: Any, bearer: String? = null): Call {
    return Request.Builder()
        .url(url)
        .post(getJsonBody(body))
        .addBearer(bearer)
        .call(this)
}