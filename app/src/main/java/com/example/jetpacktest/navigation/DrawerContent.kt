package com.example.jetpacktest.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.jetpacktest.R

@Composable
fun DrawerContent(navController: NavController, screens: Collection<Screen>, toggleDrawer: () -> Unit) {
    ModalDrawerSheet {
        Text(stringResource(R.string.app_name), modifier = Modifier.padding(16.dp))

        HorizontalDivider()

        screens.forEach { (route, name, icon) ->
            NavigationDrawerItem(
                { Text(stringResource(name)) }, // should be handled by a resource
                selected = route == navController.currentBackStackEntryAsState().value?.destination?.route,
                onClick = {
                    navController.navigate(route)
                    toggleDrawer()
                },
                icon = { Icon(icon, contentDescription = stringResource(name)) }
            )
        }
    }
}