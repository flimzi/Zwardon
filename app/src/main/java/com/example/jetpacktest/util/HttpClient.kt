package com.example.jetpacktest.util

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val httpClient by lazy {
    HttpClient(Android) {
//        expectSuccess = true

        install(Logging)
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
}

sealed class Response<out T> {
    data object Idle : Response<Nothing>()
    data object Loading : Response<Nothing>()
    data object Success : Response<Nothing>()
    data class Result<out T>(val data: T) : Response<T>()
    open class Error(val message: String) : Response<Nothing>()
    object ServerError : Error("Server Error")
}

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime =
        LocalDateTime.parse(decoder.decodeString(), formatter)

    override fun serialize(encoder: Encoder, value: LocalDateTime) =
        encoder.encodeString(formatter.format(value))
}