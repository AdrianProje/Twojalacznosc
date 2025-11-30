package com.ak.twojetlimc.mainbottomnav

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.CorporateFare
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ak.twojetlimc.R
import com.ak.twojetlimc.SettingsActivity
import com.ak.twojetlimc.ZDITMlinkhandeler
import com.ak.twojetlimc.komponenty.ClickableEmail
import com.ak.twojetlimc.komponenty.ClickablePhoneNumber
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.ImageLinkButton
import com.ak.twojetlimc.komponenty.WebsiteLink
import com.ak.twojetlimc.komponenty.downloadplanandzas
import com.ak.twojetlimc.komponenty.downlodonlyzas
import com.ak.twojetlimc.komponenty.getcurrenthour
import com.ak.twojetlimc.komponenty.switchvibrate
import com.ak.twojetlimc.planLekcji.GetList
import com.ak.twojetlimc.planLekcji.Schedule
import com.ak.twojetlimc.planLekcji.webscrapeT
import com.ak.twojetlimc.zastepstwa.Zastepstwo
import com.ak.twojetlimc.zditm.Tablicaodjazow
import com.ak.twojetlimc.zditm.getthedeparturesdata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

//-------------------------------Pomoc-------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(padding: PaddingValues) {
    val context = LocalContext.current
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = { Text(stringResource(id = R.string.MAIN_Pomoc)) },
                actions = {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, SettingsActivity::class.java))
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { paddingvalues ->
        LazyColumn(
            modifier = Modifier.padding(horizontal = 10.dp),
            contentPadding = PaddingValues(
                top = paddingvalues.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() // Combine if necessary
            )
        ) {
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 2,
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    HorizontalDivider(
                        color = Color.Black,
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier
                            .width(390.dp)
                            .heightIn(min = 200.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Text(
                                text = stringResource(id = R.string.MAIN_Pomoc_Kontakt1),
                                modifier = Modifier.padding(top = 10.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Text(
                                text = "UL. Ku Słońcu 27-30 | 71-080 Szczecin",
                                modifier = Modifier.padding(horizontal = 10.dp),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            ClickablePhoneNumber(
                                stringResource(id = R.string.MAIN_Pomoc_Kontakt2),
                                "91-48-48-056"
                            )

                            ClickablePhoneNumber(
                                stringResource(id = R.string.MAIN_Pomoc_Kontakt3),
                                "91-48-56-996"
                            )

                            ClickableEmail(email = "sekretariat@tlimc.szczecin.pl")

                        }
                    }

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.size(390.dp, 200.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.MAIN_Pomoc_Linki),
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        FlowRow(
                            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            maxItemsInEachRow = 3,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            ImageLinkButton(
                                iconId = R.drawable.lacznosc_logo_full_ia,
                                link = "https://www.tlimc.szczecin.pl/"
                            )
                            ImageLinkButton(
                                iconId = R.drawable.librus,
                                link = "https://portal.librus.pl/rodzina"
                            )
                            ImageLinkButton(
                                iconId = R.drawable.x,
                                link = "https://twitter.com/ZSLwSzczecinie"
                            )
                            ImageLinkButton(
                                iconId = R.drawable.facebook,
                                link = "https://www.facebook.com/TLiMCwSzczecinie/"
                            )
                            ImageLinkButton(
                                iconId = R.drawable.eu_logo_circle,
                                link = "https://www.facebook.com/people/Erasmus-tlimc/61555440295003/"
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
fun HelpScreenPreview() {
    HelpScreen(padding = PaddingValues(0.dp))
}

//------------------------------------Główna-----------------------------------------

@OptIn(
    ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun HomeScreen(
    navController: NavHostController,
    zditmdata: List<Int>,
    datastoremanager: Datastoremanager,
    padding: PaddingValues,
    context: Context
) {
    var showdialog by remember { mutableStateOf(false) }
    var showdialog2 by remember { mutableStateOf(false) }
    var isloadingzditm by remember { mutableStateOf(false) }

    var selectedKey by remember { mutableIntStateOf(0) }
    var refreshzditmcount by remember { mutableIntStateOf(0) }

    val listofzditm = remember { mutableStateListOf<Tablicaodjazow>() }


    val infiniteTransition = rememberInfiniteTransition(label = "infinite rotation")
    val rotationStateloading by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isloadingzditm) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    val listState = rememberLazyListState()


    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            selectedKey = (selectedKey + 1) % 4
            listState.animateScrollToItem(index = selectedKey)
        }
    } //Przewijanie losowych tekstów na pasku

    LaunchedEffect(Unit, refreshzditmcount, zditmdata) {
        while (true) {
            isloadingzditm = true
            val job = launch(Dispatchers.IO) {
                try {
                    val temporarylist = mutableListOf<Tablicaodjazow>()
                    zditmdata.forEach { number ->
                        temporarylist.add(getthedeparturesdata(number))
                    }
                    listofzditm.clear()
                    listofzditm.addAll(temporarylist)
                } catch (e: Exception) {
                    println("Error fetching data: ${e.message}")
                }
            }
            job.join()
            isloadingzditm = false
            delay(10000)
        }
    } //Pobieranie danych z ZDiTM

//TODO("Dodać tło do HomeScreen")
//    Image(
//        painter = painterResource(id = R.drawable.lacznosc_logo_full_ia),
//        contentDescription = null,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.LightGray) // Optional: Fallback color
//    )

    if (showdialog) {
        Dialog(
            onDismissRequest = { showdialog = false }) {
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
                        text = "Ta sekcja jest możliwa dzięki ZDiTM Szczecin\n",
                        textAlign = TextAlign.Center
                    )
                    Text(text = "Zostało wykorzystane poniższe API:")
                    WebsiteLink(
                        "API - tablice odjazdów",
                        "https://www.zditm.szczecin.pl/pl/zditm/dla-programistow/api-tablice-odjazdow"
                    )

                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { showdialog = false },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Super!")
                    }
                }
            }
        }
    }

    if (showdialog2) {
        Dialog(
            onDismissRequest = { showdialog2 = false }) {
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
                var texfieldvalue = rememberSaveable { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .padding(15.dp)
                        .weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Dodaj przystanek\n",
                        textAlign = TextAlign.Center
                    )
                    Text(text = "Aby dodać przystanek do tego ekranu wpisz kod przystanku poniżej")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextField(
                        value = texfieldvalue.value,
                        onValueChange = { texfieldvalue.value = it },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.2f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = { showdialog2 = false },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Anuluj")
                    }
                    Button(
                        enabled = if (texfieldvalue.value.length == 5) true else false,
                        onClick = {
                            val intent = Intent(context, ZDITMlinkhandeler::class.java)
                            intent.putExtra("zditmvalue", texfieldvalue.value)

                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Potwierdź")
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = padding
    ) {

        item {
            val context = LocalContext.current
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val assetManager = context.assets
                val inputStream = assetManager.open("Tipsu")
                val tips =
                    BufferedReader(InputStreamReader(inputStream)).readLines()
                inputStream.close()


                items(5, key = { it }) {
                    val tip by remember { mutableStateOf(tips.random()) }
                    val scrollstate = rememberScrollState(0)
                    LaunchedEffect(tip) {
                        if (scrollstate.maxValue > 0) {
                            while (true) {
                                scrollstate.animateScrollTo(
                                    value = scrollstate.maxValue,
                                    animationSpec = tween(
                                        durationMillis = 10000,
                                        easing = LinearEasing
                                    )
                                )
                                scrollstate.scrollTo(0)
                            }
                        }
                    }

                    OutlinedCard(
                        colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, Color.DarkGray),
                        modifier = Modifier
                            .fillParentMaxWidth(1f)
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = tip,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .basicMarquee(),
                                overflow = TextOverflow.Clip
                            )
                        }
                    }
                }
            }
        }

        if (SDK_INT <= Build.VERSION_CODES.S_V2) {
            item {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    border = BorderStroke(1.dp, Color.Black),
                    elevation = CardDefaults.elevatedCardElevation(10.dp),
                    modifier = Modifier
                        .fillParentMaxWidth(0.95f)
                        .wrapContentHeight()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Ta wersja Androida jest niebezpieczna!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(text = "Android ${Build.VERSION.RELEASE} nie jest już wspierany przez Google i AOSP")
                    }
                }
            }
        }

        item {
            OutlinedCard(
                onClick = { navController.navigate("whatsnew") },
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillParentMaxWidth(0.95f)
            ) {
                Text(
                    text = "Co nowego? \n Zobacz co się zmieniło w aplikacji",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxSize()
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier.fillParentMaxWidth(0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        "ZDiTM Szczecin",
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            showdialog2 = true
                        },
                        modifier = Modifier
                            .weight(0.2f)
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Info"
                        )
                    }

                    IconButton(
                        onClick = {
                            showdialog = true
                        },
                        modifier = Modifier
                            .weight(0.2f)
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info"
                        )
                    }

                    IconButton(
                        onClick = {
                            ++refreshzditmcount
                        },
                        modifier = Modifier
                            .weight(0.2f)
                            .size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Refresh,
                            contentDescription = "Refresh",
                            modifier = Modifier.rotate(rotationStateloading)
                        )
                    }
                }


                for (item in listofzditm) {
                    if (!item.departures.isEmpty()) {
                        var expanded by remember { mutableStateOf(false) }
                        val rotationState by animateFloatAsState(
                            targetValue = if (expanded) 180f else 0f,
                            label = "rotation"
                        )

                        val realtime = item.departures.first().time_real

                        val odjazd = when (realtime) {
                            0 -> {
                                "Na przystanku"
                            }

                            null -> {
                                item.departures.first().time_scheduled.toString()
                            }

                            else -> {
                                "Za: $realtime min."
                            }
                        }


                        Row(
                            modifier = Modifier
                                .clickable { expanded = !expanded }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .fillMaxHeight()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .weight(1f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "(${item.stop_number}) ${item.stop_name}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .basicMarquee(spacing = MarqueeSpacing(10.dp))
                                            .weight(1f)
                                    )
                                    Text(
                                        "| $odjazd",
                                        modifier = Modifier
                                            .padding(horizontal = 5.dp)
                                            .basicMarquee(spacing = MarqueeSpacing(10.dp))
                                            .weight(0.5f),
                                        textAlign = TextAlign.Right
                                    )
                                }

                                Text(
                                    text = "Linia: " + item.departures.first().line_number + " (${item.departures.first().direction})",
                                    modifier = Modifier.basicMarquee(spacing = MarqueeSpacing(10.dp))
                                )
                                if (item.message != null) {
                                    Text(
                                        text = item.message,
                                        modifier = Modifier.basicMarquee()
                                    )
                                }
                            }

                            IconButton(
                                onClick = { expanded = !expanded }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Rozwiń/Zwiń",
                                    modifier = Modifier.rotate(rotationState)
                                )
                            }
                        }

                        AnimatedVisibility(visible = expanded) {
                            Spacer(Modifier.weight(1f))
                            Column(modifier = Modifier.padding(10.dp)) {
                                item.departures.subList(1, item.departures.size.coerceAtMost(5))
                                    .forEach { departure ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = departure.line_number.toString(),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                            )
                                            Text(
                                                text = departure.direction.toString(),
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .basicMarquee()
                                            )
                                            val timetext = when (departure.time_real) {
                                                0 -> {
                                                    "Na przystanku"
                                                }

                                                null -> {
                                                    "${departure.time_scheduled}"
                                                }

                                                else -> {
                                                    "${departure.time_real} min."
                                                }
                                            }
                                            Text(
                                                text = timetext,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                            )
                                        }
                                    }
                                Spacer(Modifier.weight(1f))
                                val openAlertDialog = rememberSaveable { mutableStateOf(false) }
                                Button(
                                    onClick = {
                                        openAlertDialog.value = true
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "Usuń przystanek ${item.stop_number}")
                                }
                                when {
                                    openAlertDialog.value -> {
                                        AlertDialog(
                                            icon = {
                                                Icon(
                                                    Icons.Default.Clear,
                                                    contentDescription = "Ikonka usuwania"
                                                )
                                            },
                                            title = {
                                                Text(text = "Czy chcesz usunąć przystanek: ${item.stop_number} ?")
                                            },
                                            text = {
                                                Text(text = "Nie będzie można tej akcji cofnąć\nZmiany będą widoczne po restarcie aplikacji!")
                                            },
                                            onDismissRequest = {
                                                openAlertDialog.value = false
                                            },
                                            confirmButton = {
                                                Button(
                                                    onClick = {
                                                        runBlocking {
                                                            datastoremanager.deleteZditmStop(item.stop_number.toInt())
                                                            openAlertDialog.value = false
                                                            refreshzditmcount++
                                                            Toast.makeText(
                                                                context,
                                                                "Usunięto przystanek - ${item.stop_number}, uruchom ponownie aplikacje...",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                ) {
                                                    Text("Usuń")
                                                }
                                            },
                                            dismissButton = {
                                                Button(
                                                    onClick = {
                                                        openAlertDialog.value = false
                                                    }
                                                ) {
                                                    Text("Anuluj")
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

//        item {
//            OutlinedCard(
//                onClick = { /*TODO*/ },
//                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                border = BorderStroke(1.dp, Color.Black),
//                modifier = Modifier.size(390.dp, 200.dp)
//            ) {
//                Text(text = "Radio")
//            }
//        }
//
//        item {
//            OutlinedCard(
//                onClick = { /*TODO*/ },
//                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                border = BorderStroke(1.dp, Color.Black),
//                modifier = Modifier.size(390.dp, 200.dp)
//            ) {
//
//                Text(text = "Kalendarz")
//            }
//        }
//
//        item {
//            OutlinedCard(
//                onClick = { /*TODO*/ },
//                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                border = BorderStroke(1.dp, Color.Black),
//                modifier = Modifier.size(390.dp, 200.dp)
//            ) {
//
//                Text(text = "Galeria")
//            }
//        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    HomeScreen(
        navController = NavHostController(context),
        zditmdata = emptyList(),
        datastoremanager = Datastoremanager(context),
        padding = PaddingValues(0.dp),
        context = context
    )
}


//-----------------------------CO NOWEGO-----------------------------------------

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNew(navController: NavHostController, padding2: PaddingValues) {
    val context = LocalContext.current
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets.safeDrawing,
                title = {
                    Text("Co nowego?")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
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
    ) { scaffoldPadding ->
        val assetManager = context.assets
        val inputStream = assetManager.open("Whatsnew")
        val textofwhatsnew = inputStream.bufferedReader().use { it.readText() }
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .padding(
                    WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal).asPaddingValues()
                ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(
                top = scaffoldPadding.calculateTopPadding(),
                bottom = padding2.calculateBottomPadding() // Combine if necessary
            )
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
                Text(textofwhatsnew)
            }
        }
        inputStream.close()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun WhatsNewPreview() {
    val context = LocalContext.current
    WhatsNew(navController = NavHostController(context), padding2 = PaddingValues(0.dp))
}

//-----------------------------PLAN-----------------------------------------


@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "AutoboxingStateCreation"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@ExperimentalComposeUiApi
@Composable
fun PlanScreen(
    context: Context,
    accessdatastoremanager: Datastoremanager,
    vibrator: Vibrator,
    padding: PaddingValues
) {
    val connectivityManager = getSystemService(context, ConnectivityManager::class.java)
    //Zmień na jeden datastore dla całej aktywności

    val listState = rememberLazyListState() //Zapamiętaj stan listy
    val coroutineScope = rememberCoroutineScope() //Zapamiętaj stan coroutineScope
    val pullrefreshstate =
        rememberPullToRefreshState() //Zapamiętaj stan przeciągnięcia PullToRefreshState
    val sheetState = rememberModalBottomSheetState() //Zapamiętaj stan listy ModalBottomSheet


    var showBottomSheet by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var online by remember { mutableStateOf(false) }
    var onlysubstitute by remember { mutableStateOf(false) }
    var isrefresing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var planstamptext by remember { mutableStateOf("") }
    var daytext by remember { mutableStateOf("") }
    var selectedData by rememberSaveable { mutableStateOf<String?>("") }
    var selectedData2 by rememberSaveable { mutableStateOf<String?>("") }
    var klasa2 by rememberSaveable { mutableStateOf<String?>("") }

    var refreshTrigger by remember { mutableIntStateOf(0) }
    var refreshTrigger2 by remember { mutableIntStateOf(0) }
    val selectedChipOption = remember { mutableIntStateOf(0) }
    var preferedgroup by remember { mutableIntStateOf(0) }
    var currenthour by remember { mutableIntStateOf(getcurrenthour()) }

    var buttonPosition by remember { mutableStateOf<Offset?>(null) }
    var scheduleData by remember { mutableStateOf<Schedule?>(null) }

    var listitems by remember { mutableStateOf<List<Zastepstwo>?>(null) }


    var day by remember {
        mutableStateOf(
            when (LocalDate.now().dayOfWeek.value) {
                6 -> DayOfWeek.MONDAY
                7 -> DayOfWeek.MONDAY
                else -> LocalDate.now().dayOfWeek
            }
        )
    }

    LaunchedEffect(true) {
        accessdatastoremanager.getDefaultPlan.collect { defultplan ->
            selectedChipOption.intValue = defultplan ?: 0
        }
    } //Domyślny chip na liście ModalBottomSheet przy pierwszym uruchomieniu

    LaunchedEffect(true) {
        accessdatastoremanager.getPreferedGroup.collect { pref ->
            preferedgroup = pref ?: 0
        }
    }

    LaunchedEffect(true, refreshTrigger) {
        Log.d("PlanScreen", connectivityManager!!.activeNetwork.toString())
        if (connectivityManager.activeNetwork == null) {
            online = false
        } else {
            accessdatastoremanager.getOnlineMode.collect { onlinemode ->
                online = onlinemode == true
            }
        }

    } //Czy online  przy pierwszym uruchomieniu


    suspend fun loadscheduledata(): Schedule? {
        val datastore = Datastoremanager(context)

        var scheduleFromLoad: Schedule?

        try {
            val scheduleTimestamp = datastore.getPlanTimestamp.first()
            val favSchedule = datastore.getFavSchedule.first()
            onlysubstitute = datastore.getUserRefresh.first() == true

            if (selectedData2!!.isNotEmpty()) {
                klasa2 = selectedData!!.substring(0, 3)
                when (online && connectivityManager!!.activeNetwork != null) {
                    true -> {
                        Log.d(
                            "PlanScreen",
                            "loadscheduledata online (selecteddata not null)"
                        )
                        scheduleFromLoad = webscrapeT(
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/$selectedData2.html",
                            selectedData2.toString()
                        )
                    }

                    false -> {
                        online = false
                        Log.d(
                            "PlanScreen",
                            "loadscheduledata offline (selecteddata not null)"
                        )
                        scheduleFromLoad = datastore.getnewchosenClass(
                            context,
                            scheduleTimestamp.toString(),
                            selectedData2.toString(),
                            1
                        )
                    }
                }
            } else {
                if (favSchedule!!.isNotEmpty()) {
                    selectedData = favSchedule.split(",")[0]
                    selectedData2 = favSchedule.split(",")[1]
                    when (online && connectivityManager!!.activeNetwork != null) {
                        true -> {
                            Log.d(
                                "PlanScreen",
                                "loadscheduledata online (favschedule not null) $selectedData, $selectedData2"
                            )
                            scheduleFromLoad = webscrapeT(
                                "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/$selectedData2.html",
                                selectedData2.toString()
                            )
                        }

                        false -> {
                            online = false
                            Log.d(
                                "PlanScreen",
                                "loadscheduledata offline (favschedule not null)"
                            )

                            Log.d("PlanScreen", " \"$scheduleTimestamp/$selectedData2\"")
                            scheduleFromLoad = datastore.getnewchosenClass(
                                context, scheduleTimestamp.toString(),
                                selectedData2!!, 1
                            )
                        }
                    }
                } else {
                    online = false
                    Log.d(
                        "PlanScreen",
                        "loadscheduledata offline (selecteddata not null)"
                    )
                    scheduleFromLoad = datastore.getnewSchedule(
                        context,
                        scheduleTimestamp.toString(),
                        1
                    )?.first()
                    selectedData = scheduleFromLoad?.imieinazwisko ?: ""
                }
            }
        } catch (e: Exception) {
            Log.e("PlanScreen", "Error loading schedule: ${e.message}")
            scheduleFromLoad = null
        }



        Log.d("PlanScreen", "Result: $scheduleFromLoad")
        return scheduleFromLoad
    }

    suspend fun loadsubstitue(): List<Zastepstwo>? {
        val substitutelist = mutableListOf<Zastepstwo>()
        for (i in 1..13) {
            val thedate =
                LocalDate.now().with(TemporalAdjusters.nextOrSame(day)).toString()
            try {
                val datastore = Datastoremanager(context)
                val data1 = datastore.getZastepstwo(
                    context, i, klasa2.toString(),
                    thedate, 1
                )
                val data2 = datastore.getZastepstwo(
                    context, i, "$klasa2(1)",
                    thedate, 1
                )
                val data3 = datastore.getZastepstwo(
                    context, i, "$klasa2(2)",
                    thedate, 1
                )
                if (data1 != null) {
                    substitutelist.add(data1)
                }
                if (data2 != null) {
                    substitutelist.add(data2)
                }
                if (data3 != null) {
                    substitutelist.add(data3)
                }
            } catch (_: Exception) {
                Log.d(
                    "PLANLOADING",
                    "Nie znaleziono zastępstwa dla danej lekcji"
                )
                null
            }
        }
        return substitutelist
    }

    val pattern = longArrayOf(0, 50)

    val options = stringArrayResource(R.array.chip_values)

    val daynames = stringArrayResource(R.array.days)

    LaunchedEffect(key1 = currenthour) {
        try {
            if (day == LocalDate.now().dayOfWeek) {
                listState.animateScrollToItem(index = currenthour, scrollOffset = -600)
            }
        } catch (e: Exception) {
            Log.d("PlanScreen", "Błąd animacji $e")
        }
    } //Scrollowanie na karcie planu lekcji

    LaunchedEffect(refreshTrigger2) {
        sheetState.hide()
        showBottomSheet = false
    } //Schowaj BottomSheet


    when (day.value) {
        1 -> daytext = daynames[0]
        2 -> daytext = daynames[1]
        3 -> daytext = daynames[2]
        4 -> daytext = daynames[3]
        5 -> daytext = daynames[4]
    }


    val density = LocalDensity.current
    val offsetX = with(density) { buttonPosition?.x?.toDp() ?: 0.dp }
    val offsetY = with(density) { buttonPosition?.y?.toDp() ?: 0.dp }

    val lifecycleOwner = LocalLifecycleOwner.current

    //lista opcji do wybrania po otworzeniu BottomAppBar

    LaunchedEffect(selectedData, refreshTrigger, online) {
        refreshTrigger2++
        isLoading = true
        isrefresing = true
        coroutineScope.launch(Dispatchers.IO) {
            scheduleData = loadscheduledata()
            isLoading = false
            isrefresing = false
        }
    } //Odświerzanie planu po zmienieniu jednej z zmiennej

    LaunchedEffect(selectedData, refreshTrigger, day) {
        isLoading = true
        isrefresing = true
        coroutineScope.launch(Dispatchers.IO) {
            listitems = loadsubstitue()
            isLoading = false
            isrefresing = false
        }
    } //Odświerzanie zastępstw po zmienieniu jednej z zmiennej

    LaunchedEffect(true) {
        while (true) {
            currenthour = getcurrenthour()
            delay(60000)
        }
    } //Aktualizacja aktualnej godziny lekcyjnej do scrolowania

    val startRefresh: () -> Unit = {
        isrefresing = true
        coroutineScope.launch {
            Log.d("PLANLOADING", "Refresh")
            if (!isLoading) {
                try {
                    if (onlysubstitute) {
                        val workrequest = downlodonlyzas(context)
                        WorkManager.getInstance(context)
                            .getWorkInfoByIdLiveData(workrequest.id)
                            .observe(
                                lifecycleOwner, Observer { status ->
                                    if (status!!.state == WorkInfo.State.SUCCEEDED) {
                                        isrefresing = false
                                        refreshTrigger++
                                        Toast.makeText(
                                            context,
                                            "Odświeżono",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("PLANLOADING", "$refreshTrigger")
                                    }
                                })
                    } else {
                        Log.d("PLANLOADING", "else")
                        val workrequest = downloadplanandzas(context)
                        WorkManager.getInstance(context)
                            .getWorkInfoByIdLiveData(workrequest.id)
                            .observe(
                                lifecycleOwner, Observer { status ->
                                    if (status!!.state == WorkInfo.State.SUCCEEDED) {
                                        isrefresing = false
                                        refreshTrigger++
                                        Toast.makeText(
                                            context,
                                            "Odświeżono",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.d("PLANLOADING", "$refreshTrigger")
                                    }
                                })
                    }
                } catch (e: Exception) {
                    Log.d("PLANLOADING", "$e")
                }
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(bottom = padding.calculateBottomPadding())
                    .border(1.dp, Color.Black, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium),
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f),
                contentPadding = PaddingValues(horizontal = 10.dp),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center // Fill the content area of the BottomAppBar
                ) { // Blurred Background - Sits at the back of this Box
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .blur(
                                radius = 95.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (selectedData != "") {
                                Log.d("PLANLOADING", selectedData.toString())
                                selectedData.toString().substring(0, 5) + "..."
                            } else {
                                ""
                            }
                        )

                        Spacer(Modifier.weight(1f))

                        Button(
                            onClick = { showDropdownMenu = true },
                            modifier = Modifier
                                .onGloballyPositioned {
                                    buttonPosition = it.positionInRoot()
                                }
                                .padding(horizontal = 10.dp)

                        ) {
                            Text(
                                daytext,
                                modifier = Modifier
                                    .basicMarquee(spacing = MarqueeSpacing(10.dp))

                            )
                        }

                        Button(
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            Text(
                                stringResource(id = R.string.PLAN_Button_Zmień),
                                modifier = Modifier
                                    .basicMarquee(spacing = MarqueeSpacing(10.dp))

                            )
                        }

                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = {
                                showDropdownMenu = false
                            },
                            offset = DpOffset(x = offsetX, y = offsetY)
                        ) {
                            daynames.forEach {
                                DropdownMenuItem(
                                    text = { Text(text = it) },
                                    onClick = {
                                        showDropdownMenu = false
                                        when (it) {
                                            daynames[0] -> day = DayOfWeek.MONDAY
                                            daynames[1] -> day = DayOfWeek.TUESDAY
                                            daynames[2] -> day = DayOfWeek.WEDNESDAY
                                            daynames[3] -> day = DayOfWeek.THURSDAY
                                            daynames[4] -> day = DayOfWeek.FRIDAY
                                        }
                                        daytext = it
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        //Wyświetlane ekrany w zależności od opcji

        PullToRefreshBox(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
            state = pullrefreshstate,
            isRefreshing = isrefresing,
            onRefresh = {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                startRefresh()
            }
        ) {
            if (scheduleData != null) {
                scheduleData?.let { it1 ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = padding
                    ) {

                        if (preferedgroup != 0) item {
                            OutlinedCard(
                                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                                border = BorderStroke(1.dp, Color.DarkGray),
                                modifier = Modifier
                                    .fillParentMaxWidth(0.95f)
                                    .wrapContentHeight()
                            ) {
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "Widzisz aktualnie plan tylko dla grupy: $preferedgroup",
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        it1.plan.forEach {
                            val numerlekcji = it.numerLekcji
                            val czas = it.czas
                            val dzien = it.dzien
                            val klasa = it.klasa
                            val detale = it.detale


                            Log.d("PLANLOADING", "Zastępstwo: $listitems")

                            if (dzien == day.ordinal) {
                                item {
                                    OutlinedCard(
                                        modifier = Modifier
                                            .heightIn(min = 130.dp)
                                            .padding(vertical = 5.dp)
                                            .fillParentMaxWidth(0.95f)
                                            .layoutId(numerlekcji)
                                            .animateContentSize(),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .weight(1f)
                                                .background(
                                                    if (numerlekcji == currenthour && day == LocalDate.now().dayOfWeek) {
                                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                                    } else {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    }
                                                ),
                                            horizontalArrangement = Arrangement.SpaceEvenly
                                        ) {
                                            Column(
                                                Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(0.3f),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    text = "$numerlekcji \n-\n $czas",
                                                    textAlign = TextAlign.Center
                                                )
                                            }

                                            val filteredDetale = when (preferedgroup) {
                                                1 -> {
                                                    detale.filter { detail ->
                                                        // Show if:
                                                        // 1. Lesson name contains "-1/2" (specifically for group 1)
                                                        // 2. Lesson name does NOT contain "-1/2" AND does NOT contain "-2/2" (for both groups)
                                                        detail.przedmiot.contains("-1/2") ||
                                                                (!detail.przedmiot.contains("-1/2") && !detail.przedmiot.contains(
                                                                    "-2/2"
                                                                ))
                                                    }
                                                }

                                                2 -> {
                                                    detale.filter { detail ->
                                                        // Show if:
                                                        // 1. Lesson name contains "-2/2" (specifically for group 2)
                                                        // 2. Lesson name does NOT contain "-1/2" AND does NOT contain "-2/2" (for both groups)
                                                        detail.przedmiot.contains("-2/2") ||
                                                                (!detail.przedmiot.contains("-1/2") && !detail.przedmiot.contains(
                                                                    "-2/2"
                                                                ))
                                                    }
                                                }

                                                else -> { // preferedgroup == 0 or any other value
                                                    // Show all lessons
                                                    detale
                                                }
                                            }

                                            for (details in filteredDetale) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Column(
                                                    modifier = Modifier
                                                        .weight(3f)
                                                        .fillMaxHeight(),
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(
                                                                if (numerlekcji == currenthour && day == LocalDate.now().dayOfWeek) {
                                                                    MaterialTheme.colorScheme.surfaceContainerHighest
                                                                } else {
                                                                    MaterialTheme.colorScheme.primaryContainer
                                                                }
                                                            ),
                                                        horizontalArrangement = Arrangement.Center
                                                    ) {
                                                        if (details.sala != "") {
                                                            Text(text = details.sala)
                                                        }
                                                    }

                                                    Column(
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .fillMaxWidth()
                                                            .background(
                                                                MaterialTheme.colorScheme.background,
                                                                MaterialTheme.shapes.medium
                                                            ),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        if (details.przedmiot.isNotBlank()) Text(
                                                            text = details.przedmiot,
                                                            textAlign = TextAlign.Center
                                                        )

                                                        if (details.nauczyciel.isNotBlank()) Text(
                                                            text = details.nauczyciel,
                                                            textAlign = TextAlign.Center
                                                        )

                                                        if (klasa.isNotBlank()) Text(
                                                            text = klasa,
                                                            textAlign = TextAlign.Center
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(2.dp))
                                            }
                                        }
                                        if (numerlekcji == currenthour && day == LocalDate.now().dayOfWeek) {
                                            val endingtime = LocalTime.parse(
                                                czas.split("-")[1].filter { !it.isWhitespace() },
                                                DateTimeFormatter.ofPattern("H:mm")
                                            )
                                            var minutesLeft by remember { mutableLongStateOf(0L) }

                                            LaunchedEffect(key1 = endingtime) {
                                                while (LocalTime.now().isBefore(endingtime)) {
                                                    minutesLeft = ChronoUnit.MINUTES.between(
                                                        LocalTime.now(),
                                                        endingtime
                                                    ) + 1
                                                    delay(1000) // Wait for 1 second
                                                }
                                                // Once the lesson is over, you might want to set minutes to 0
                                                minutesLeft = 0
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(
                                                        if (numerlekcji == currenthour && day == LocalDate.now().dayOfWeek) {
                                                            MaterialTheme.colorScheme.surfaceContainerHighest
                                                        } else {
                                                            MaterialTheme.colorScheme.primaryContainer
                                                        }
                                                    ),
                                                horizontalArrangement = Arrangement.Center
                                            ) {

                                                if (minutesLeft < 45) {
                                                    Text(text = "Pozostało $minutesLeft min. lekcji")
                                                } else if (minutesLeft == 0L) {
                                                    Text(text = "Lekcja się skończyła")
                                                } else {
                                                    Text(text = "Pozostało ${minutesLeft - 45} min. przerwy")
                                                }
                                            }
                                        }
                                    }

                                    if (!listitems.isNullOrEmpty() && dzien == day.ordinal) {
                                        listitems!!.forEach { zastdata2 ->
                                            if (numerlekcji == zastdata2.numerLekcji) {
                                                OutlinedCard(
                                                    modifier = Modifier
                                                        .padding(vertical = 5.dp)
                                                        .height(65.dp)
                                                        .fillParentMaxWidth(0.95f),
                                                    shape = MaterialTheme.shapes.medium,
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                                                ) {
                                                    Row(
                                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                                        modifier = Modifier
                                                            .align(Alignment.CenterHorizontally)
                                                            .fillMaxSize()
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            modifier = Modifier
                                                                .align(Alignment.CenterVertically)
                                                                .weight(0.43f)
                                                        ) {
                                                            Text(
                                                                text = "⬆️",
                                                                textAlign = TextAlign.Center,
                                                            )
                                                        }
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            modifier = Modifier
                                                                .align(Alignment.CenterVertically)
                                                                .weight(1f)
                                                        ) {
                                                            if (zastdata2.klasa.isNotEmpty()) Text(
                                                                text = zastdata2.klasa,
                                                                textAlign = TextAlign.Center
                                                            )

                                                            if (zastdata2.zastepca.isNotEmpty()) Text(
                                                                text = zastdata2.zastepca,
                                                                textAlign = TextAlign.Center
                                                            )

                                                            if (zastdata2.uwagi.isNotEmpty()) Text(
                                                                text = zastdata2.uwagi,
                                                                textAlign = TextAlign.Center
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (showBottomSheet) {
                        val listdata = GetList(selectedChipOption.intValue, context)
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState,
                            modifier = Modifier.statusBarsPadding()
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(
                                    text = "Tryb online",
                                    modifier = Modifier
                                        .padding(horizontal = 10.dp)
                                        .align(Alignment.CenterVertically)
                                )
                                Switch(
                                    checked = online,
                                    onCheckedChange = {
                                        switchvibrate(context)
                                        online = it
                                    }
                                )
                            }

                            val focusManager = LocalFocusManager.current
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                runBlocking {
                                    planstamptext =
                                        accessdatastoremanager.getPlanTimestamp.first().toString()
                                }
                                if (planstamptext != "") {
                                    Text(text = "⬇️ Offline: $planstamptext")
                                } else {
                                    Text(text = "Brak pobranego planu lekcji")
                                }
                            }
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                items(options) { option ->
                                    FilterChip(
                                        selected = when (selectedChipOption.intValue) {
                                            0 -> option == options[0]
                                            1 -> option == options[1]
                                            2 -> option == options[2]
                                            3 -> option == options[3]
                                            else -> {
                                                false
                                            }
                                        },
                                        onClick = {
                                            selectedChipOption.intValue = when (option) {
                                                options[0] -> 0
                                                options[1] -> 1
                                                options[2] -> 2
                                                options[3] -> 3
                                                else -> {
                                                    0
                                                }
                                            }
                                        },
                                        label = { Text(option) },
                                        leadingIcon = {
                                            val imageVector = when (option) {
                                                options[0] -> Icons.Filled.Clear
                                                options[1] -> Icons.Filled.Star
                                                options[2] -> Icons.Rounded.CorporateFare
                                                options[3] -> Icons.Filled.AccountCircle
                                                else -> null
                                            }
                                            if (imageVector != null) {
                                                Icon(
                                                    imageVector = imageVector,
                                                    contentDescription = "Option"
                                                )
                                            }
                                        }
                                    )
                                }
                            }


                            var searchQuery by remember { mutableStateOf("") }
                            val filteredData = listdata.filter {
                                it?.imieinazwisko?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true
                            }

                            TextField(
                                value = searchQuery,
                                onValueChange = {
                                    searchQuery = it
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .align(Alignment.CenterHorizontally)
                                    .padding(bottom = 10.dp),
                                label = { Text(text = "Wyszukaj") },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Search
                                ),
                                keyboardActions = KeyboardActions(
                                    onSearch = {
                                        focusManager.clearFocus()
                                    }
                                ),
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                Icons.Filled.Close,
                                                contentDescription = "Wyczyść wyszukiwanie"
                                            )
                                        }
                                    }
                                },
                                shape = MaterialTheme.shapes.medium,
                                enabled = true
                            )

                            LazyColumn(
                                Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(filteredData) { data ->
                                    Button(
                                        shape = MaterialTheme.shapes.medium,
                                        modifier = Modifier
                                            .fillParentMaxWidth(0.9f),
                                        onClick = {
                                            selectedData = data?.imieinazwisko
                                            selectedData2 = data?.htmlvalue
                                        }
                                    ) {
                                        data?.imieinazwisko?.let { it1 -> Text(it1) }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                online = false
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Brak pobranego planu lekcji")
                    Button(
                        onClick = {
                            onlysubstitute = false
                            startRefresh()
                        }
                    ) {
                        Text("Spróbuj pobrać plan")
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@ExperimentalComposeUiApi
@androidx.compose.ui.tooling.preview.Preview
@Composable
fun PlanScreenPreview() {
    val context = LocalContext.current
    PlanScreen(
        context = context,
        accessdatastoremanager = Datastoremanager(context),
        vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator,
        padding = PaddingValues(0.dp)
    )
}