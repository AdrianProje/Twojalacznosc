package com.ak.twojetlimc

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.AlarmManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.Permissionsinfo
import com.ak.twojetlimc.komponenty.createalarm
import com.ak.twojetlimc.komponenty.downloadplanandzas
import com.ak.twojetlimc.planLekcji.GetList
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
        fun NavGraph(navController: NavHostController, padding: PaddingValues) {
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
                    Scaffold(
                        bottomBar = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.safeDrawing)
                                    .padding(horizontal = 10.dp)
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
                                                                    navController.navigate("OBBE_konfiguracja")
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
                                                                if (navController.currentBackStackEntry?.destination?.route == "OBBE_start") {
                                                                    navController.navigate("OBBE_pobieranie")
                                                                }
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
                    ) { padding ->
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = padding
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
                        }
                    }
                }

                composable(route = "OBBE_terms") {
                    Scaffold(
                        bottomBar = {
                            Button(
                                onClick = { navController.navigate("OBBE_start") },
                                Modifier
                                    .fillMaxWidth(1f)
                                    .windowInsetsPadding(WindowInsets.safeDrawing)
                                    .padding(horizontal = 10.dp),
                                shape = MaterialTheme.shapes.medium,
                            ) {
                                Text(text = stringResource(id = R.string.OBBE_Back))
                            }
                        }
                    ) { padding ->
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = padding
                        ) {
                            item {
                                val assetManager = contextu.assets
                                val inputStream = assetManager.open("terms")
                                val terms = inputStream.bufferedReader().use { it.readText() }
                                Text(text = terms)
                                inputStream.close()
                            }
                        }
                    }

                }

                composable(route = "OBBE_pobieranie") {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
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
                            .padding(horizontal = 10.dp)
                            .padding(padding),
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

                composable(route = "OBBE_konfiguracja") {
                    val scheduleData by remember { mutableStateOf(GetList(0, contextu)) }

                    var checkedoption by remember { mutableStateOf("") }

                    Scaffold(
                        topBar = {
                            Column(
                                modifier = Modifier
                                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    stringResource(id = R.string.SETTINGS_Button_DomysnyPlan_Opis),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .windowInsetsPadding(WindowInsets.safeDrawing)
                                        .padding(10.dp),
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        "Brak (Domyślne)",
                                        modifier = Modifier
                                            .weight(1f)
                                            .align(Alignment.CenterVertically)
                                    )
                                    Checkbox(
                                        checked = checkedoption == "",
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) {
                                                checkedoption = ""
                                            }
                                        }
                                    )
                                }
                            }
                        },
                        bottomBar = {
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .windowInsetsPadding(WindowInsets.safeDrawing)
                                    .padding(horizontal = 10.dp),
                                onClick = {
                                    if (checkedoption != "") {
                                        serviceScope.launch {
                                            accessdatastoremanager.saveFavSchedule(checkedoption)
                                            accessdatastoremanager.saveFavScheduleOnOff(true)
                                        }
                                    }
                                    navController.navigate("OBBE_Permissions")
                                }
                            ) {
                                Text("Przejdź dalej")
                            }
                        }
                    ) { padding ->
                        LazyColumn(
                            contentPadding = padding
                        ) {
                            scheduleData.forEach { item ->
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(onClick = {
                                                checkedoption =
                                                    item!!.imieinazwisko + "," + item.htmlvalue
                                            })
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = item!!.imieinazwisko,
                                            modifier = Modifier
                                                .weight(1f)
                                                .align(Alignment.CenterVertically)
                                        )
                                        Checkbox(
                                            checked = checkedoption == item.imieinazwisko + "," + item.htmlvalue,
                                            onCheckedChange = { isChecked ->
                                                if (isChecked) {
                                                    checkedoption =
                                                        item.imieinazwisko + "," + item.htmlvalue
                                                } else {
                                                    if (checkedoption == item.imieinazwisko) checkedoption =
                                                        ""
                                                    else checkedoption
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                composable(route = "OBBE_Permissions") {
                    val alarmManager =
                        applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
                    requestPermissions(
                        arrayOf(
                            POST_NOTIFICATIONS
                        ), 1
                    )
                    when {
                        ContextCompat.checkSelfPermission(
                            this@OBBE,
                            POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED && alarmManager.canScheduleExactAlarms() -> {
                            createalarm(applicationContext)
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(obbetomaintent)
                        }

                        else -> {
                            Scaffold(
                                bottomBar = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .windowInsetsPadding(WindowInsets.safeDrawing)
                                            .padding(horizontal = 10.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Button(
                                                onClick = {
                                                    startActivity(
                                                        Intent(
                                                            ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                                            "package:${applicationContext.packageName}".toUri()
                                                        )
                                                    )
                                                },
                                                shape = MaterialTheme.shapes.medium,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = "Przyznaj Alarm")
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
                                                shape = MaterialTheme.shapes.medium,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = "Przyznaj Instalowanie")
                                            }
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            Button(
                                                onClick = {
                                                    createalarm(applicationContext)
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                                    startActivity(obbetomaintent)
                                                },
                                                shape = MaterialTheme.shapes.medium,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text(text = "Przejdź do aplikacji")
                                            }
                                        }
                                    }
                                }
                            ) { padding ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 10.dp)
                                        .padding(padding),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Permissionsinfo()
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
                Scaffold(
                    contentWindowInsets = WindowInsets.safeDrawing,
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    val navController = rememberNavController()
                    NavGraph(navController, padding)
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