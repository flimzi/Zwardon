package com.example.jetpacktest.routes

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.jetpacktest.R

val NamedNavArgument.parameter get() = "{$name}"
fun NamedNavArgument.getInt(entry: NavBackStackEntry) = entry.arguments?.getInt(name)!!

val NavBackStackEntry.actualRoute: String?
    get() = destination.route?.let {
        it.split("/").joinToString("/") { segment ->
            if (segment.startsWith("{") && segment.endsWith("}"))
                arguments?.get(segment.removeSurrounding("{", "}"))?.toString() ?: segment
            else
                segment
        }
    }

data class Route(val route: String, val parameters: List<NamedNavArgument> = listOf(), val icon: ImageVector = Icons.Default.Favorite, val name: @Composable () -> Unit = { }) {
    fun replace(vararg arguments: Any) = parameters.zip(arguments).fold(route) { acc, (parameter, argument) ->
        acc.replace(parameter.parameter, argument.toString())
    }.let { Route(it) }

    operator fun plus(nested: Route) = nested.copy(route = route + "/" + nested.route, parameters = parameters + nested.parameters)
}

fun NavController.navigate(route: Route, vararg arguments: Any) = navigate(route.replace(arguments).route)

data class ParameterBuilder(val parameters: MutableList<NamedNavArgument> = mutableListOf()) {
    fun param(parameter: NamedNavArgument): String {
        parameters.add(parameter)
        return parameter.parameter
    }
}

fun route(builder: ParameterBuilder.() -> String, icon: ImageVector = Icons.Default.Favorite, name: @Composable () -> Unit = { }): Route {
    val parameterBuilder = ParameterBuilder()
    return Route(parameterBuilder.builder(), parameterBuilder.parameters, icon, name)
}

fun route(route: String, icon: ImageVector = Icons.Default.Favorite, @StringRes name: Int)
    = Route(route, icon = icon, name = { Text(stringResource(name)) })

object App {
    val home = route("home", Icons.Default.Home, R.string.home)
    val loading = Route("loading")

    object Authentication {
        val login = route("login", Icons.Default.Lock, R.string.login)
        val register = route("register", Icons.Default.Lock, R.string.register)
    }

    object User {
        private val user = Route("user")
        val userId = navArgument("userId") { type = NavType.IntType }

        val add = user + route("add", Icons.Default.AddCircle, R.string.addUser)
        val logout = user + route("logout", Icons.Default.Close, R.string.login)
        val id = user + route({ param(userId) })
        val edit = id + route("edit", Icons.Default.Edit, R.string.login)
        val delete = id + route("delete", Icons.Default.Clear, R.string.login)
    }

    object Task {
        private val task = Route("task")
        val taskId = navArgument("taskId") { type = NavType.IntType }

        val id = task + route({ param(taskId) })
        val add = User.id + task + route("add", Icons.Default.AddCircle, R.string.addUser)
    }
}

fun NavGraphBuilder.singleIntParameterRoute(route: Route, content: @Composable (NavBackStackEntry, Int) -> Unit)
    = this.composable(route.route, route.parameters.toList()) {
        content(it, route.parameters[0].getInt(it))
    }

fun NavGraphBuilder.doubleIntParameterRoute(route: Route, content: @Composable (NavBackStackEntry, Int, Int) -> Unit)
    = this.composable(route.route, route.parameters.toList()) {
        content(it, route.parameters[0].getInt(it), route.parameters[1].getInt(it))
    }

//fun <T: Fetch<T>> NavGraphBuilder.fetchSingle(route: Route, snackbarHostState: SnackbarHostState, content: @Composable (NavBackStackEntry, T) -> Unit)
//    = this.singleIntParameterRoute(route) { entry, id ->
//
//    }