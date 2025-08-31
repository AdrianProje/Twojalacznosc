package com.ak.twojetlimc

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.WebsiteLink
import com.ak.twojetlimc.komponenty.downloadplanandzas
import com.ak.twojetlimc.komponenty.instalnewversion
import com.ak.twojetlimc.komponenty.isthereanewversion
import com.ak.twojetlimc.mainbottomnav.HelpScreen
import com.ak.twojetlimc.mainbottomnav.HomeScreen
import com.ak.twojetlimc.mainbottomnav.MainNavItems
import com.ak.twojetlimc.mainbottomnav.PlanScreen
import com.ak.twojetlimc.mainbottomnav.WhatsNew
import com.ak.twojetlimc.theme.AppTheme
import kotlinx.coroutines.flow.first

class MainScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val accessdata = Datastoremanager(this)
        val vibrator = applicationContext.getSystemService(Vibrator::class.java)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //Tworzenie kanału powiadomień, nie można zmienić ważności powiadomienia
        //Kanał powiadomień 1: Powiadomienie o następej lekcji
        val name = getString(R.string.NOTIFICATION_Schedule)
        val mChannel = NotificationChannel("PLAN", name, NotificationManager.IMPORTANCE_HIGH)
        mChannel.enableVibration(true)
        mChannel.description = getString(R.string.NOTIFICATION_Schedule_ExtraText)
        notificationManager.createNotificationChannel(mChannel)

        //Kanał powiadomień 2: Powiadomienie o pobieraniu planu lekcji
        val name2 = "Powiadomienie o trwającym pobieraniu planu lekcji"
        val mChannel2 =
            NotificationChannel("POBIERANIEPLANU", name2, NotificationManager.IMPORTANCE_LOW)
        mChannel2.description = "Powiadomienie pokazywane w trakcie pobierania planu lekcji"
        notificationManager.createNotificationChannel(mChannel2)

        val name3 = "Powiadomienie o zastępstwie"
        val mChannel3 =
            NotificationChannel("ZASTEPSTWO", name3, NotificationManager.IMPORTANCE_HIGH)
        mChannel3.enableVibration(true)
        mChannel3.description = "Powiadomienie pokazywane gdy będzie zastępstwo"
        notificationManager.createNotificationChannel(mChannel3)

        val name4 = "Powiadomienie o zmianie planu lekcji"
        val mChannel4 =
            NotificationChannel("ZMIANAPLANU", name4, NotificationManager.IMPORTANCE_HIGH)
        mChannel4.enableVibration(true)
        mChannel4.description = "Powiadomienie pokazywane gdy plan lekcji zostanie zmieniony"
        notificationManager.createNotificationChannel(mChannel4)

        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced =
            false
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                val contextu = LocalContext.current

                var updateready by remember { mutableStateOf(false) }

                LaunchedEffect(key1 = accessdata) {
                    val value = accessdata.getUPObbe.first()
                    val paranoia = accessdata.getParanoia.first()
                    if (value == false) {
                        Log.d(
                            "Main_Screen",
                            "Datastore odczytany - START AKTYWNOŚCI / Pobieranie danych"
                        )
                        startActivity(Intent(applicationContext, OBBE::class.java))
                    } else {
                        Log.d("Main_Screen", "Datastore odczytany - PRZEJŚCIE DALEJ")
                        if (paranoia == true) {
                            downloadplanandzas(contextu)
                            Log.d("Main_Screen", "Uruchomiono paranoje")
                        }
                    }
                }  //Sprawdzanie czy aplikacja włączona jest pierwszy raz

                LaunchedEffect(key1 = Unit) {
                    if (!contextu.packageManager.getPackageInfo(
                            contextu.packageName,
                            0
                        ).versionName.toString()
                            .endsWith("debug")
                    ) {
                        updateready = isthereanewversion(contextu)
                    }
                } //Sprawdzanie czy jest nowa wersja aplikacji

                val destination = when (intent.getStringExtra("destination")) {
                    "plan" -> {
                        "plan"
                    }

                    else -> {
                        "home"
                    }
                }

                NotificationManagerCompat.from(this).cancel(1)
                // remember navController so it does not
                // get recreated on recomposition
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    // Bottom navigation
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    }
                ) { innerPadding ->
                    if (updateready && contextu.packageManager.canRequestPackageInstalls()) {
                        val context = LocalContext.current
                        Dialog(onDismissRequest = { }) {
                            Card(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                                border = BorderStroke(1.dp, Color.Black),
                                elevation = CardDefaults.elevatedCardElevation(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(10.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .weight(0.8f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nowa aktualizacja jest dostępna!\n",
                                        textAlign = TextAlign.Center
                                    )
                                    Text(text = "Nowa aktualizacja dodaje nowe funkcje, poprawia bezpieczeństwo oraz pozwala jeszcze bardziej cieszyć się aplikacją!\n")
                                    WebsiteLink(
                                        "Pobierz ręcznie",
                                        "https://www.tlimc.szczecin.pl/Apka/TwojaLacznosc.apk"
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.2f),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    Button(
                                        onClick = { instalnewversion(context) },
                                        modifier = Modifier.padding(8.dp),
                                    ) {
                                        Text("Zainstaluj")
                                    }
                                }
                            }
                        } //Można zainstalować aktualizację
                    } else if (updateready) {
                        Dialog(onDismissRequest = { updateready = false }) {
                            Card(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                border = BorderStroke(1.dp, Color.Black),
                                elevation = CardDefaults.elevatedCardElevation(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(10.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(15.dp)
                                        .weight(0.8f)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Nowa aktualizacja jest dostępna!\nBrak uprawnień do instalacji\n",
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Nowa aktualizacja dodaje nowe funkcje, poprawia bezpieczeństwo oraz pozwala jeszcze bardziej cieszyć się aplikacją!\n\nPrzyznaj uprawnienia aby zainstalować, lub",
                                        textAlign = TextAlign.Center
                                    )
                                    WebsiteLink(
                                        "Pobierz ręcznie",
                                        "https://www.tlimc.szczecin.pl/Apka/TwojaLacznosc.apk"
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(0.2f)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        onClick = { updateready = false },
                                        modifier = Modifier.padding(horizontal = 5.dp),
                                    ) {
                                        Text("Anuluj")
                                    }
                                    Button(
                                        onClick = {
                                            startActivity(
                                                Intent(
                                                    ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                                    "package:${applicationContext.packageName}".toUri()
                                                )
                                            )
                                        },
                                        modifier = Modifier.padding(horizontal = 5.dp),
                                    ) {
                                        Text("Przyznaj instalowanie")
                                    }
                                }
                            }
                        } //Aktualizacja dostępna ale brak uprawnień
                    }
                    NavHostContainer(
                        navController = navController,
                        destination,
                        vibrator,
                        innerPadding
                    )
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
    val gradient = Brush.verticalGradient(
        0f to MaterialTheme.colorScheme.primaryContainer,
        1f to MaterialTheme.colorScheme.primary
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    ) {

        MainNavItems().forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,

                // przejście do konkretnej ścierzki na przycisk
                onClick = {
                    if (currentRoute != navItem.route) {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
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
    destination: String,
    vibrator: Vibrator,
    padding: PaddingValues
) {
    val context = LocalContext.current
    NavHost(
        modifier = Modifier
            .padding(bottom = padding.calculateBottomPadding()),
        navController = navController,
        startDestination = destination,
        enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
        builder = {

            // ścierzka pomoc
            composable("pomoc") {
                HelpScreen()

            }

            // ścierzka główna
            composable("home") {
                HomeScreen(navController, padding)
            }

            composable("whatsnew") {
                WhatsNew(navController)
            }

            // ścierzka plan
            composable("plan") {
                PlanScreen(context, vibrator)
            }
        }
    )
}