package com.ak.twojetlimc.mainbottomnav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.ak.twojetlimc.R

@Composable
fun MainNavItems(): List<MainNavItem> {
    val customIconPainter = painterResource(id = R.drawable.logo_awka_zegar_bez_kropek)
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
            icon = Icons.AutoMirrored.Default.List,
            route = "plan"
        )
    )
    return items
}