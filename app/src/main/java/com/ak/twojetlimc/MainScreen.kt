package com.ak.twojetlimc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.mainbottomnav.HelpScreen
import com.ak.twojetlimc.mainbottomnav.HomeScreen
import com.ak.twojetlimc.mainbottomnav.MainNavItems
import com.ak.twojetlimc.mainbottomnav.PlanScreen
import com.ak.twojetlimc.theme.AppTheme
import kotlinx.coroutines.flow.first

class MainScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val accessdata = Datastoremanager(this)

        //Tworzenie kanału powiadomień, nie można zmienić ważności powiadomienia
        val name = getString(R.string.NOTIFICATION_Schedule)
        val descriptionText = getString(R.string.NOTIFICATION_Schedule_ExtraText)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel("PLAN", name, importance)
        mChannel.description = descriptionText

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val name2 = "Powiadomienie o trwającym pobieraniu planu lekcji"
        val descriptionText2 = "Powiadomienie pokazywane w trakcie pobierania planu lekcji"
        val mChannel2 = NotificationChannel("POBIERANIEPLANU", name2, importance)
        mChannel.description = descriptionText2

        val notificationManager2 = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager2.createNotificationChannel(mChannel2)

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        enableEdgeToEdge()
        setContent {
            if (Build.VERSION.SDK_INT >= 29) {
                window.isNavigationBarContrastEnforced = false
            }
            AppTheme {
                val navController = rememberNavController()
                //Sprawdzanie czy aplikacja włączona jest pierwszy raz
                val contextu = LocalContext.current

                LaunchedEffect(key1 = accessdata) {
                    val value = accessdata.getUPObbe.first()
                    if (value == false) {
                        val przejscie = Intent(applicationContext, OBBE::class.java)
                        Log.d(
                            "Main_Screen",
                            "Datastore odczytany - START AKTYWNOŚCI / Pobieranie danych"
                        )
                        startActivity(przejscie)
                    } else {
                        Log.d("Main_Screen", "Datastore odczytany - PRZEJŚCIE DALEJ")
                    }
                }

                var destination = intent.getStringExtra("destination")
                if (destination == null) {
                    destination = "home"
                } else {
                    NotificationManagerCompat.from(this).cancel(1)
                }
                // remember navController so it does not
                // get recreated on recomposition

                Scaffold(
                    // Bottom navigation
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    NavHostContainer(navController = navController, innerPadding, destination)
                }

            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // observe the backstack
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // observe current route to change the icon
    // color,label color when navigated
    val currentRoute = navBackStackEntry?.destination?.route
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {

        MainNavItems().forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,

                // przejście do konkretnej ścierzki na przycisk
                onClick = {
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route)
                    }
                },

                // Icon of navItem
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = navItem.label,
                        tint = if (currentRoute == navItem.route) {
                            // Set the color for the selected state
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            // Set the color for the unselected state
                            MaterialTheme.colorScheme.inversePrimary
                        }
                    )
                },

                // label
                label = { Text(text = navItem.label) },

                alwaysShowLabel = false
            )
        }
    }
    if (currentRoute != "plan") {
        HorizontalDivider(
            color = Color.Black,
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    destination: String
) {
    val context = LocalContext.current

    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        navController = navController,

        // główna ścierzka aplikacji do "home"
        startDestination = destination,
        builder = {

            // ścierzka pomoc
            composable("pomoc") {
                HelpScreen()
            }

            // ścierzka główna
            composable("home") {
                HomeScreen()
            }

            // ścierzka plan
            composable("plan") {
                PlanScreen(context)
            }
        })
}