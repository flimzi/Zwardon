package com.example.jetpacktest.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.jetpacktest.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController, action: Screen, toggleDrawer: () -> Unit) {
    CenterAlignedTopAppBar(
        { Text(stringResource(R.string.app_name)) },
        navigationIcon = {
            IconButton({ toggleDrawer() }) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton({ navController.navigate(action.route) }) {
                Icon(action.icon, contentDescription = stringResource(action.name))
            }
        }
    )
}
