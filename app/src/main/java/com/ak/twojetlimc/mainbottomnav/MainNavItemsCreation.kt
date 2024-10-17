package com.ak.twojetlimc.mainbottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ak.twojetlimc.R

@Composable
fun MainNavItems() : List<MainNavItem> {
    val items = listOf(
        MainNavItem(
            label = stringResource(id = R.string.MAIN_Pomoc),
            icon = Icons.Filled.Info,
            route = "pomoc"
        ),
        MainNavItem(
            label = stringResource(id = R.string.MAIN_Dom),
            icon = Icons.Filled.Home,
            route = "home"
        ),
        MainNavItem(
            label = stringResource(id = R.string.MAIN_Plan),
            icon = Icons.Filled.Favorite,
            route = "plan"
        )
    )
    return items
}