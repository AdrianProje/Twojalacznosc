package com.ak.twojetlimc

import android.Manifest.permission.POST_NOTIFICATIONS
import android.Manifest.permission.SCHEDULE_EXACT_ALARM
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.createalarm
import com.ak.twojetlimc.komponenty.downloadplanandzas
import com.ak.twojetlimc.theme.AppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class OBBE : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        val serviceScope = CoroutineScope(Dispatchers.IO)
        val obbetomaintent = Intent(applicationContext, MainScreen::class.java)
        val vibrator = applicationContext.getSystemService(Vibrator::class.java)
        val accessdatastoremanager = Datastoremanager(this)

        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @Composable
        fun NavGraph(navController: NavHostController) {
            val contextu = LocalContext.current
            var checked by remember { mutableStateOf(false) }
            val effect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
            val listState = rememberLazyListState()
            val connectivityManager = getSystemService(ConnectivityManager::class.java)
            NavHost(
                navController = navController,
                startDestination = "OBBE_start",
            ) {
                composable(route = "OBBE_start") {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp)
                            .systemBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        item {
                            Image(
                                painter = painterResource(id = R.drawable.lacznosclogo),
                                contentDescription = R.string.OBBE_Zdjecie_text.toString(),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(300.dp),
                            )
                        }

                        item {
                            Text(
                                text = stringResource(id = R.string.OBBE_text),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center
                            )
                        }


                        item {
                            Row {
                                Text(
                                    stringResource(id = R.string.OBEE_WarunkiZgoda),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(16.dp)
                                )
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {
                                        checked = it
                                        vibrator.vibrate(effect)
                                    }
                                )
                            }
                        }

                        item {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Button(
                                    onClick = { navController.navigate("OBBE_terms") },
                                    modifier = Modifier.fillMaxWidth(0.5f),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(stringResource(id = R.string.OBBE_ShowTerms))
                                }
                                Button(
                                    onClick = {
                                        if (checked) {
                                            if (connectivityManager.activeNetwork != null) {
                                                val workrequest = downloadplanandzas(contextu)

                                                Log.d("OBBE", "Praca rozpoczęta")
                                                WorkManager.getInstance(contextu)
                                                    .getWorkInfoByIdLiveData(workrequest.id)
                                                    .observe(this@OBBE) { workInfo ->
                                                        when (workInfo?.state) {
                                                            WorkInfo.State.SUCCEEDED -> {
                                                                if (connectivityManager.activeNetwork != null) {
                                                                    serviceScope.launch {
                                                                        accessdatastoremanager.saveUPObbe(
                                                                            true
                                                                        )
                                                                    }
                                                                    Log.d(
                                                                        "OBBE",
                                                                        "Zakończono pracę pomyślnie, przechodznie do przyznania powiadomień"
                                                                    )
                                                                    navController.navigate("OBBE_Permissions")
                                                                } else {
                                                                    Log.d("OBBE", "Błąd")
                                                                    navController.navigate("OBBE_pob_error")
                                                                }
                                                            }

                                                            WorkInfo.State.ENQUEUED -> {
                                                                Log.d("OBBE", "ENQUEUED")
                                                            }

                                                            WorkInfo.State.RUNNING -> {
                                                                Log.d("OBBE", "Trwa Praca")
                                                                navController.navigate("OBBE_pobieranie")
                                                            }

                                                            WorkInfo.State.FAILED -> {
                                                                Log.d("OBBE", "Błąd")
                                                                navController.navigate("OBBE_pob_error")
                                                            }

                                                            WorkInfo.State.BLOCKED -> {
                                                                Log.d("OBBE", "Zablokowano")
                                                            }

                                                            WorkInfo.State.CANCELLED -> {
                                                                Log.d("OBBE", "Cancelled")
                                                            }

                                                            null -> {
                                                                Log.d("OBBE", "Nieznany satus")
                                                            }
                                                        }
                                                    }
                                            } else {
                                                navController.navigate("OBBE_pob_error")
                                            }
                                        } else {
                                            Toast.makeText(
                                                baseContext,
                                                R.string.OBBE_BrakZgody,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(1f),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Text(stringResource(id = R.string.OBBE_Button_Next))
                                }
                            }
                        }
                    }
                }

                composable(route = "OBBE_terms") {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .safeDrawingPadding()
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Text(text = stringResource(id = R.string.OBBE_WarunkiAplikacji))
                        }
                        item {
                            Button(
                                onClick = { navController.navigate("OBBE_start") },
                                Modifier.fillMaxWidth(1f), shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(text = stringResource(id = R.string.OBBE_Back))
                            }
                        }
                    }
                }

                composable(route = "OBBE_pobieranie") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.MAIN_Loading),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                composable(route = "OBBE_pob_error") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(horizontal = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Wystąpił błąd podczas pobierania danych\n Sprawdź połączenie z internetem i spróbuj ponownie.",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Button(
                            onClick = { navController.navigate("OBBE_start") },
                            Modifier.fillMaxWidth(1f), shape = MaterialTheme.shapes.medium,
                        ) {
                            Text(text = "Spróbuj ponownie")
                        }
                    }
                }

                composable(route = "OBBE_Permissions") {
                    val alarmManager =
                        applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
                    requestPermissions(arrayOf(POST_NOTIFICATIONS, SCHEDULE_EXACT_ALARM), 1)
                    when {
                        ContextCompat.checkSelfPermission(
                            this@OBBE,
                            POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED && alarmManager.canScheduleExactAlarms() -> {

                            createalarm(applicationContext)

                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(obbetomaintent)
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .safeDrawingPadding()
                                    .padding(horizontal = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Aplikacja korzysta z uprawnień powiadamiania oraz planowania alarmów",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = "\n\n- Potrzebujemy twojego zezwolenia na powiadomienia (do powiadamiania o planie lekcji oraz innych ważnych rzeczach)\n\n- Potrzebujemy twojego zezwolenia na ustawianie alarmów, aby aplikacja mogła w tle sprawdzać nadchodzące lekcje i wykonywać odpowiednie czynności związane z nimi\n\nZezwolenia zawsze możesz wycofać później w ustawieniach systemu",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .weight(1f),
                                    contentAlignment = Alignment.BottomStart

                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Button(
                                            onClick = {
                                                createalarm(applicationContext)
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                startActivity(obbetomaintent)
                                            },
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier.weight(0.5f)
                                        ) {
                                            Text(text = "Przejdź do aplikacji")
                                        }
                                        Button(
                                            onClick = {
                                                startActivity(
                                                    Intent(
                                                        ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                                    )
                                                )
                                            },
                                            shape = MaterialTheme.shapes.medium,
                                            modifier = Modifier.weight(0.5f)
                                        ) {
                                            Text(text = "Przyznaj teraz")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced =
            false
        enableEdgeToEdge()
        setContent {

            AppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    NavGraph(navController)
                    BackHandler(true) {
                        if (navController.currentBackStackEntry?.destination?.route == "OBBE_start") {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}