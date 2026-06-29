package com.kira.kmpbase

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import com.kira.kmpbase.core.navigation.AppDestination
import com.kira.kmpbase.core.navigation.BottomNavItem
import com.kira.kmpbase.core.ui.generated.resources.Res
import com.kira.kmpbase.core.ui.generated.resources.nav_home
import com.kira.kmpbase.core.ui.generated.resources.nav_settings
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AppBottomNavBar(
    navController: NavHostController,
    currentDestination: NavDestination?,
) {
    BottomNavBar {
        BottomNavItems(
            currentDestination = currentDestination,
            onItemClick = { destination ->
                navController.navigate(destination) {
                    popUpTo(AppDestination.Home) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
        )
    }
}

@Composable
private fun RowScope.BottomNavItems(
    currentDestination: NavDestination?,
    onItemClick: (AppDestination) -> Unit,
) {
    BottomNavItem.entries.forEach { item ->
        val selected = currentDestination?.hierarchy?.any {
            it.hasRoute(item.destination::class)
        } == true
        val label = item.localizedLabel()

        NavigationBarItem(
            selected = selected,
            onClick = { onItemClick(item.destination) },
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                )
            },
            label = { Text(label) },
        )
    }
}

@Composable
private fun BottomNavItem.localizedLabel(): String {
    return stringResource(
        when (this) {
            BottomNavItem.Home -> Res.string.nav_home
            BottomNavItem.Settings -> Res.string.nav_settings
        },
    )
}
