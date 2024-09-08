package com.example.jetpacktest.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jetpacktest.authentication.AuthViewModel
import com.example.jetpacktest.util.Response
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.flow

class TaskViewModel(private val authViewModel: AuthViewModel) : ViewModel() {
    private val tasks = mutableMapOf<Int, Task>()

    fun get(taskId: Int) = flow {
        if (tasks.containsKey(taskId)) {
            emit(Response.Result(tasks[taskId]!!))
        } else {
            emit(Response.Loading)

            Api.Events.get(authViewModel.accessToken.value!!, eventId = taskId).let {
                if (it.status != HttpStatusCode.OK) {
                    emit(Response.ServerError)
                } else {
                    val task = it.body<Task>()
                    tasks[taskId] = task
                    emit(Response.Result(task))
                }
            }
        }
    }

    fun get(taskId: Int?) = flow<Response<Task>> { emit(Response.Idle) }

    fun add(userId: Int, task: Task) = flow {
        emit(Response.Loading)

        Api.Events.add(authViewModel.accessToken.value!!, userId, task).let {
            if (it.status == HttpStatusCode.Created)
                emit(Response.Result(it.body<Int>()))
            else
                emit(Response.ServerError)
        }
    }
}

class TaskViewModelFactory(private val authViewModel: AuthViewModel) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = TaskViewModel(authViewModel) as T
}