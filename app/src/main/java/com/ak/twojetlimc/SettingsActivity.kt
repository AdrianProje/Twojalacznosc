package com.ak.twojetlimc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.updateAll
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ak.twojetlimc.AppWidget.WidgetDumbUI
import com.ak.twojetlimc.komponenty.ClickableEmail
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.Permissionsinfo
//import com.ak.twojetlimc.komponenty.LiveNotification
import com.ak.twojetlimc.komponenty.PlanCheck
import com.ak.twojetlimc.komponenty.RefreshWorker
import com.ak.twojetlimc.komponenty.ZasCheck
import com.ak.twojetlimc.komponenty.createalarm
import com.ak.twojetlimc.komponenty.switchvibrate
import com.ak.twojetlimc.planLekcji.GetList
import com.ak.twojetlimc.theme.AppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi")
@Composable
fun NavGraph(
    navController: NavHostController,
    context: Context,
    vibrator: Vibrator,
    datastoremanager: Datastoremanager,
    paddingvalues: PaddingValues
) {
    val scheduleData = GetList(0, context)
    var expanded by remember { mutableStateOf(false) }
    var expanded2 by remember { mutableStateOf(false) }
    var cheched by remember { mutableStateOf(false) }
    var cheched2 by remember { mutableStateOf(false) }
    var cheched3 by remember { mutableStateOf(false) }
    var cheched4 by remember { mutableStateOf(false) }
    var favschedulevalue by remember { mutableStateOf("") }
    val pattern = longArrayOf(0, 50)

    val options = stringArrayResource(id = R.array.chip_values)
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
                favschedulevalue = (datastoremanager.getFavSchedule.first() ?: "").split(",")[0]
                cheched = datastoremanager.getFavScheduleOnOff.first() == true
                cheched2 = datastoremanager.getParanoia.first() == true
                cheched3 = datastoremanager.getUserRefresh.first() == true
                cheched4 = datastoremanager.getOnlineMode.first() == true

                datastoremanager.getDefaultPlan.collect { defultplan ->
                    when (defultplan) {
                        0 -> selectedchip = options[0]
                        1 -> selectedchip = options[1]
                        2 -> selectedchip = options[2]
                        3 -> selectedchip = options[3]
                    }
                }
            }

            fun quicksafe(int: Int) {
                scope.launch { datastoremanager.saveDefaultPlan(int) }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = paddingvalues,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
            ) {
                item {
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                }
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
                                text = stringResource(id = R.string.SETTINGS_Button_DomysnyChip_Opis) + selectedchip,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = {
                                expanded = false
                            }
                        ) {
                            stringArrayResource(R.array.chip_values).forEachIndexed { index, s ->
                                DropdownMenuItem(
                                    text = { Text(text = s) },
                                    onClick = {
                                        quicksafe(index)
                                        selectedchip = options[index]
                                        expanded = false
                                    }
                                )
                            }
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
                                    text = stringResource(id = R.string.SETTINGS_Button_DomysnyPlan_Opis) + favschedulevalue,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            DropdownMenu(
                                expanded = expanded2,
                                onDismissRequest = {
                                    expanded2 = false
                                }, modifier = Modifier.safeContentPadding()
                            ) {
                                scheduleData.forEach { data ->
                                    DropdownMenuItem(
                                        text = { Text(text = data!!.imieinazwisko) },
                                        onClick = {
                                            expanded2 = false
                                            scope.launch {
                                                datastoremanager.saveFavSchedule(data!!.imieinazwisko + "," + data.htmlvalue)
                                                WidgetDumbUI().updateAll(context)
                                            }
                                            favschedulevalue = data!!.imieinazwisko
                                        })
                                }
                            }
                        }

                        Switch(
                            checked = cheched,
                            onCheckedChange = {
                                switchvibrate(context)
                                scope.launch {
                                    if (it) {
                                        cheched = true
                                        datastoremanager.saveFavScheduleOnOff(true)
                                        datastoremanager.saveFavSchedule(scheduleData.first()!!.imieinazwisko + "," + scheduleData.first()!!.htmlvalue)
                                        WidgetDumbUI().updateAll(context)
                                        favschedulevalue = scheduleData.first()!!.imieinazwisko

                                    } else {
                                        cheched = false
                                        datastoremanager.saveFavScheduleOnOff(false)
                                        datastoremanager.saveFavSchedule("")
                                        favschedulevalue = ""
                                    }
                                    WidgetDumbUI().updateAll(context)
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
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(top = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Paranoja_Opis),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(bottom = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Switch(
                            checked = cheched2,
                            onCheckedChange = {
                                switchvibrate(context)
                                scope.launch {
                                    if (it) {
                                        cheched2 = true
                                        datastoremanager.saveParanoia(true)
                                    } else {
                                        cheched2 = false
                                        datastoremanager.saveParanoia(false)
                                    }
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
                            colors = if (cheched3) {
                                CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                CardDefaults.outlinedCardColors(containerColor = Color.Gray)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.SETTINGS_ZASTEPSTWA_REFRESH_TYTUL),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(top = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_ZASTEPSTWA_REFRESH_OPIS),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(bottom = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Switch(checked = cheched3, onCheckedChange = {
                            switchvibrate(context)
                            scope.launch {
                                if (it) {
                                    cheched3 = true
                                    datastoremanager.saveUserRefresh(true)
                                } else {
                                    cheched3 = false
                                    datastoremanager.saveUserRefresh(false)
                                }
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
                            colors = if (cheched4) {
                                CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primary)
                            } else {
                                CardDefaults.outlinedCardColors(containerColor = Color.Gray)
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Online_Tytul),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(top = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                text = stringResource(id = R.string.SETTINGS_Online_Opis),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 25.dp)
                                    .padding(bottom = 5.dp),
                                color = contentColorFor(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Switch(checked = cheched4, onCheckedChange = {
                            switchvibrate(context)
                            scope.launch {
                                if (it) {
                                    cheched4 = true
                                    datastoremanager.saveOnlineMode(true)
                                } else {
                                    cheched4 = false
                                    datastoremanager.saveOnlineMode(false)
                                }
                            }
                        })
                    }
                }

//                item {
//                    Button(
//                        onClick = {
//                            try {
//                                val appWidgetManager =
//                                    AppWidgetManager.getInstance(context)
//                                val myProvider = ComponentName(context, WidgetDumbUI::class.java)
//
//                                val successCallback = PendingIntent.getBroadcast(
//                                    context,
//                                    32784, // requestCode
//                                    Intent("com.ak.twojetlimc.ACTION_WIDGET_PINNED"),
//                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//                                )
//                                appWidgetManager.requestPinAppWidget(
//                                    myProvider,
//                                    null,
//                                    successCallback
//                                )
//                            } catch (e: Exception) {
//                                Log.d("SettingsActivity", "Błąd dodawania widgetu $e")
//                            }
//                        },
//                        modifier = Modifier.fillParentMaxWidth(),
//                        shape = MaterialTheme.shapes.medium,
//                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
//                    ) {
//                        Text(
//                            text = "Dodaj widget do ekranu startowego",
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                    }
//                }

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
                        onClick = { navController.navigate("terms") },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = stringResource(R.string.SETTINGS_Button_Terms),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                item {
                    Button(
                        onClick = { navController.navigate("permissionsinfo") },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            text = "Uprawnienia",
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
                    Text(
                        text = stringResource(id = R.string.SETTINGS_Wersja) + "$version - (Beta)"
                    )
                    ClickableEmail("developer.adriank@gmail.com")
                }

                if (context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).versionName.toString().endsWith("debug")
                ) {
                    item {
                        Button(
                            onClick = { navController.navigate("debug") },
                            modifier = Modifier.fillParentMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = "Debug",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        composable(route = "powiadomienia") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = paddingvalues,
                modifier = Modifier
                    .fillMaxSize()
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
                contentPadding = paddingvalues,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
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
                    Text(text = "- Adam Jankowski 5tB\n Betatester aplikacji")
                }
                item {
                    Text(text = "- Kamil Zagórski (Absolwent)\n Betatester aplikacji")
                }
                item {
                    Text(text = "- Julka Januszek (Absolwentka)\n Ikony do aplikacji")
                }
            }
        }

        composable(route = "permissionsinfo") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = paddingvalues,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    Text(
                        text = "Uprawnienia",
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                    )
                }
                item {
                    Permissionsinfo()
                }
            }
        }

        composable(route = "terms") {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = paddingvalues,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.SETTINGS_Button_Terms),
                        style = MaterialTheme.typography.titleLarge
                    )
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                    )
                }
                item {
                    val inputStream = context.assets.open("terms")
                    val terms = inputStream.bufferedReader().use { it.readText() }
                    Text(text = terms)
                    inputStream.close()
                }
            }
        }

        composable(route = "debug") {
            var groupedlist by remember { mutableStateOf(mapOf<String, List<String>>()) }
            LaunchedEffect(key1 = Unit) {
                scope.launch {
                    groupedlist = datastoremanager.getAllScheduleKeysGrouped()
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = paddingvalues,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxSize()
            ) {
                item {
                    Text(text = "Debug menu:", style = MaterialTheme.typography.titleLarge)
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                item {
                    Text(
                        "Aktualna wersja (kod): ${
                            context.packageManager.getPackageInfo(
                                context.packageName,
                                0
                            ).longVersionCode
                        }"
                    )
                }

                item {
                    Button(
                        onClick = {
                            WorkManager.getInstance(context)
                                .beginWith(OneTimeWorkRequestBuilder<RefreshWorker>().build())
                                .enqueue()
                        },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "DEBUG | Przetestuj powiadomienia \n (Ustaw odpowiednią godzinę w smartfonie i sprawdź czy przyjdzie ci powiadomienie)")
                    }
                }

//                item {
//                    Button(
//                        onClick = {
//                            WorkManager.getInstance(context)
//                                .beginWith(OneTimeWorkRequestBuilder<LiveNotification>().build())
//                                .enqueue()
//                        },
//                        modifier = Modifier.fillParentMaxWidth(),
//                        shape = MaterialTheme.shapes.medium,
//                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
//                    ) {
//                        Text(text = "DEBUG | Przetestuj powiadomienia na żywo\n (Ustaw odpowiednią godzinę w smartfonie i sprawdź czy przyjdzie ci powiadomienie na żywo w odpowiednim miejscu)")
//                    }
//                }

                item {
                    Button(
                        onClick = {

                            WorkManager.getInstance(context)
                                .beginWith(OneTimeWorkRequestBuilder<ZasCheck>().build())
                                .enqueue()
                        },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "DEBUG | Pobierz zastępstwa")
                    }
                }

                item {
                    Button(
                        onClick = {

                            WorkManager.getInstance(context)
                                .beginWith(OneTimeWorkRequestBuilder<PlanCheck>().build())
                                .enqueue()
                        },
                        modifier = Modifier.fillParentMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "DEBUG | Pobierz plan lekcji")
                    }
                }

                item {
                    Button(
                        onClick = {
                            createalarm(context)
                        },
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        Text(
                            text = "DEBUG | Napraw wyświetlanie powiadomień"
                        )
                    }
                }

                item {
                    Column {
                        Text("Debug | Zarządzanie zapisanymi planami")
                        groupedlist.entries.reversed().forEach { (groupName, keys) ->
                            var expanded2 by remember { mutableStateOf(false) }
                            val rotationState by animateFloatAsState(
                                targetValue = if (expanded) 180f else 0f,
                                label = "rotation"
                            )
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { expanded2 = !expanded2 }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = groupName)
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(onClick = {
                                        scope.launch {
                                            datastoremanager.savePlanTimestamp(groupName)
                                        }
                                    }) {
                                        Text("Zmień datę")
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Button(onClick = {
                                        scope.launch {
                                            keys.forEach { key ->
                                                Log.d("SettingsActivity - Debug", "Usunięto: $key")
                                                datastoremanager.deleteDataFromStringPreferencesKey(
                                                    key
                                                )
                                            }
                                        }
                                    }) {
                                        Text("Usuń wszy.")
                                    }
                                    IconButton(onClick = { expanded2 = !expanded2 }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowDropDown,
                                            contentDescription = "Expand/Collapse",
                                            modifier = Modifier.rotate(rotationState)
                                        )
                                    }
                                }

                                AnimatedVisibility(visible = expanded2) {
                                    Column(modifier = Modifier.padding(start = 16.dp)) {
                                        keys.forEach { key ->
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = key)
                                                Spacer(modifier = Modifier.weight(1f))
                                                Button(onClick = {
                                                    scope.launch {
                                                        datastoremanager.deleteDataFromStringPreferencesKey(
                                                            key
                                                        )
                                                    }
                                                }) {
                                                    Text("Usuń")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Image(
                        painter = painterResource(id = R.drawable.cukierki_ciut),
                        contentDescription = "Debug",
                        contentScale = ContentScale.FillBounds,
                        alignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                    )
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
                                IconButton(
                                    onClick = {
                                        if (currentBackStackEntry?.destination?.route == "mainsettings") {
                                            finish()
                                        } else {
                                            navController.popBackStack()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = "Powrót"
                                    )
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    NavGraph(navController, context, vibrator, datastoremanager, paddingValues)
                }
            }
        }
    }
}