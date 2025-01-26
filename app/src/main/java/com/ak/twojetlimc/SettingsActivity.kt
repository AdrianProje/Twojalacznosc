package com.ak.twojetlimc

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ak.twojetlimc.PlanLekcji.GetList
import com.ak.twojetlimc.komponenty.ClickableEmail
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.createalarm
import com.ak.twojetlimc.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NavGraph(
    navController: NavHostController,
    context: Context,
    vibrator: Vibrator,
    datastoremanager: Datastoremanager
) {
    val scheduleData = GetList(0, context)
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var cheched by remember { mutableStateOf(false) }
    var cheched2 by remember { mutableStateOf(false) }
    var cheched3 by remember { mutableStateOf(false) }
    var favschedulevalue by remember { mutableStateOf("") }
    val pattern = longArrayOf(0, 50)

    val options = listOf(
        stringResource(id = R.string.PLAN_Chip_Klasa),
        stringResource(id = R.string.PLAN_Chip_Sala),
        stringResource(id = R.string.PLAN_Chip_Nauczyciel)
    )
    var selectedchip by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = "mainsettings",
        enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
    ) {

        composable(route = "mainsettings") {
            LaunchedEffect(key1 = Unit) {
                cheched = datastoremanager.getFavScheduleOnOff.first() == true
                cheched2 = datastoremanager.getParanoia.first() == true
                cheched3 = datastoremanager.getUserRefresh.first() == true
                favschedulevalue = (datastoremanager.getFavSchedule.first() ?: "").split(",")[0]
                datastoremanager.getDefaultPlan.collect { defultplan ->
                    when (defultplan) {
                        1 -> selectedchip = options[0]
                        2 -> selectedchip = options[1]
                        3 -> selectedchip = options[2]
                    }
                }
            }

            fun quicksafe(int: Int) {
                scope.launch { datastoremanager.saveDefaultPlan(int) }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        onClick = { expanded = true },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(modifier = Modifier.fillMaxWidth(1f)) {
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Button_DomysnyChip),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Button_DomysnyChip_Opis) + "\n\n$selectedchip",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.PLAN_Chip_Klasa)) },
                                onClick = {
                                    quicksafe(1)
                                    selectedchip = options[0]
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.PLAN_Chip_Sala)) },
                                onClick = {
                                    quicksafe(2)
                                    selectedchip = options[1]
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(text = stringResource(id = R.string.PLAN_Chip_Nauczyciel)) },
                                onClick = {
                                    quicksafe(3)
                                    selectedchip = options[2]
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = {
                                if (cheched) {
                                    expanded2 = true
                                } else {
                                    Toast.makeText(context, "Włącz funkcję", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            },
                            colors = if (cheched) {
                                ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                            } else {
                                ButtonDefaults.buttonColors(Color.Gray)
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentHeight()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(1f)) {
                                Text(
                                    text = stringResource(id = R.string.SETTING_Button_DomyslnyPlan),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = stringResource(id = R.string.SETTINGS_Button_DomysnyPlan_Opis) + "\n\n$favschedulevalue",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            DropdownMenu(
                                expanded = expanded2,
                                onDismissRequest = {
                                    expanded2 = false
                                }) {
                                scheduleData.forEach { data ->
                                    DropdownMenuItem(
                                        text = { Text(text = data!!.imieinazwisko) },
                                        onClick = {
                                            expanded2 = false
                                            scope.launch { datastoremanager.saveFavSchedule(data!!.imieinazwisko + "," + data.htmlvalue) }
                                            favschedulevalue = data!!.imieinazwisko
                                        })
                                }
                            }
                        }

                        Switch(
                            checked = cheched,
                            onCheckedChange = {
                                if (it) {
                                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                    cheched = true
                                    scope.launch { datastoremanager.saveFavScheduleOnOff(true) }
                                    scope.launch { datastoremanager.saveFavSchedule(scheduleData.first()!!.imieinazwisko + "," + scheduleData.first()!!.htmlvalue) }
                                    favschedulevalue = scheduleData.first()!!.imieinazwisko

                                } else {
                                    vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                    cheched = false
                                    scope.launch { datastoremanager.saveFavSchedule("") }
                                    favschedulevalue = ""
                                    scope.launch { datastoremanager.saveFavScheduleOnOff(false) }
                                }
                            }
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f)
                                .wrapContentHeight(),
                            colors = if (cheched2) {
                                CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                CardDefaults.outlinedCardColors(containerColor = Color.Gray)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Paranoja_Tytul),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Paranoja_Opis),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Switch(checked = cheched2, onCheckedChange = {
                            if (it) {
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                cheched2 = true
                                scope.launch { datastoremanager.saveParanoia(true) }
                            } else {
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                cheched2 = false
                                scope.launch { datastoremanager.saveParanoia(false) }
                            }
                        })
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Card(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f)
                                .wrapContentHeight(),
                            colors = if (cheched3) {
                                CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                CardDefaults.outlinedCardColors(containerColor = Color.Gray)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.SETTINGS_ZASTEPSTWA_REFRESH_TYTUL),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 10.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_ZASTEPSTWA_REFRESH_OPIS),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Switch(checked = cheched3, onCheckedChange = {
                            if (it) {
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                cheched3 = true
                                scope.launch { datastoremanager.saveUserRefresh(true) }
                            } else {
                                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                                cheched3 = false
                                scope.launch { datastoremanager.saveUserRefresh(false) }
                            }
                        })
                    }
                }

                item {
                    Button(
                        onClick = { navController.navigate("powiadomienia") },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Ustawienia powiadomienień",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item {
                    Button(
                        onClick = { navController.navigate("podziekowania") },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(id = R.string.SETTINGS_Podziekowanie),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item {
                    val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                    val version = pInfo.versionName
                    Text(text = stringResource(id = R.string.SETTINGS_Wersja) + "$version - (Beta)")
                    ClickableEmail("developer.adriank@gmail.com")
                }
            }
        }

        composable(route = "powiadomienia") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    Text(
                        text = "Ustawienia powiadomienień:",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Left
                    )
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    item {
                        Button(
                            onClick = {
                                createalarm(context)

                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillParentMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(1f)) {
                                Text(
                                    text = "Napraw wyświetlanie powiadomień",
                                    style = MaterialTheme.typography.titleLarge,
                                )
                                Text(
                                    text =
                                    "(Potrzebna zgoda na ustawianie alarmów oraz zgoda na powiadomienia)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }


//                item {
//                    Button(
//                        onClick = {
//                            val workRequest =
//                                OneTimeWorkRequestBuilder<RefreshWorker>().build()
//                            val workRequest2 =
//                                OneTimeWorkRequestBuilder<ZasCheck>().build()
//
//                            WorkManager.getInstance(context)
//                                .beginWith(workRequest)
//                                .then(workRequest2)
//                                .enqueue()
//                        },
//                        modifier = Modifier.fillParentMaxWidth(),
//                        shape = MaterialTheme.shapes.medium,
//                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
//                    ) {
//                        Text(text = "DEBUG | Przetestuj powiadomienia | Pobierz nowy plan oraz zastępstwa\n (Ustaw odpowiednią godzinę w smartfonie i sprawdź czy przyjdzie ci powiadomienie)")
//                    }
//                }

                item {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, "com.ak.twojetlimc")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    Button(
                        onClick = { context.startActivity(intent) },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Otwórz systemowe ustawienia powiadomień",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        composable(route = "podziekowania") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                item {
                    Text(text = "Podziękowania:", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
                item {
                    Text(text = "- Adam Jankowski 4tB\n Betatester aplikacji")
                }
                item {
                    Text(text = "- Julka Januszek 5tF\n Ikony do aplikacji")
                }
            }
        }
    }
}

class SettingsActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) window.isNavigationBarContrastEnforced =
            false
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val context = LocalContext.current
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val vibrator = applicationContext.getSystemService(Vibrator::class.java)
                val datastoremanager = Datastoremanager(context)
                LaunchedEffect(key1 = datastoremanager) {
                    if (datastoremanager.getUPObbe.first() == false) {
                        startActivity(Intent(applicationContext, OBBE::class.java))
                    }
                }
                Scaffold(
                    modifier = Modifier
                        .displayCutoutPadding(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(stringResource(id = R.string.SETTINGS_BAR_TEXT))
                            },
                            navigationIcon = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                    contentDescription = "Powrót",
                                    modifier = Modifier
                                        .clickable {
                                            if (currentBackStackEntry?.destination?.route == "mainsettings") {
                                                finish()
                                            } else {
                                                navController.popBackStack()
                                            }
                                        }
                                        .size(30.dp)
                                )
                            }
                        )
                    }
                ) { paddingValues ->
                    Surface(modifier = Modifier.padding(paddingValues)) {
                        HorizontalDivider(
                            color = Color.Black,
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth()
                        )
                        NavGraph(navController, context, vibrator, datastoremanager)
                    }
                }
            }
        }
    }
}