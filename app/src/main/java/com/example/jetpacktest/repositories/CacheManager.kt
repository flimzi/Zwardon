package com.example.jetpacktest.repositories

import kotlin.reflect.KType
import kotlin.reflect.typeOf

// i need a way to discern different requests so that for example calling for children of user 2 is not mixed up with children of user 4 because its the same api request
// and the parameters are baked in the flow so they cannot be accessed after the flow has been constructed so to speak by the Api class
// and response class does not hold any identifying information only the result of the operation
// so i need to insert the caching setup probably in the httpClient request builder because i know more then
// but assuming this is only relevant for get requests, i think the only thing i need is the uri for example /user/20/related/children
// and it would ideally

data class CacheEntry(val timestamp: Long, val type: KType, val data: Any?) {
    companion object {
        inline fun <reified T> create(data: T) = CacheEntry(System.currentTimeMillis(), typeOf<T>(), data)
    }
}

class CacheManager {
    val cache = mutableMapOf<String, CacheEntry>()

    inline fun <reified T> put(key: String, value: T) {
        cache[key] = CacheEntry.create(value)
    }
}