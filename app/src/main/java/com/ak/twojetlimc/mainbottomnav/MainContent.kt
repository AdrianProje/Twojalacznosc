package com.ak.twojetlimc.mainbottomnav

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ak.twojetlimc.PlanLekcji.GetList
import com.ak.twojetlimc.PlanLekcji.Schedule
import com.ak.twojetlimc.PlanLekcji.webscrapeT
import com.ak.twojetlimc.R
import com.ak.twojetlimc.SettingsActivity
import com.ak.twojetlimc.Zastepstwa.Zastepstwo
import com.ak.twojetlimc.komponenty.ClickableEmail
import com.ak.twojetlimc.komponenty.ClickablePhoneNumber
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.ImageLinkButton
import com.ak.twojetlimc.komponenty.RefreshWorker
import com.ak.twojetlimc.komponenty.downloadplanandzas
import com.ak.twojetlimc.komponenty.downlodonlyzas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.toString


//-------------------------------Pomoc-------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen() {
    val context = LocalContext.current
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
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
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = paddingvalues
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
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.size(390.dp, 200.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = stringResource(id = R.string.MAIN_Pomoc_Kontakt1),
                            modifier = Modifier.padding(vertical = 10.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "UL. Ku Słońcu 27-30 | 71-080 Szczecin",
                            modifier = Modifier.padding(horizontal = 10.dp),
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
            }

//                    item {
//                        OutlinedCard(
//                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
//                            border = BorderStroke(1.dp, Color.Black),
//                            modifier = Modifier.size(390.dp, 200.dp)
//                        ) {
//                            Row(
//                                modifier = Modifier
//                                    .align(Alignment.CenterHorizontally)
//                                    .padding(vertical = 50.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                Image(
//                                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
//                                    contentDescription = "hello"
//                                )
//
//                                Text(
//                                    text = stringResource(id = R.string.MAIN_Pomoc_Dyrektor),
//                                    textAlign = TextAlign.Center,
//                                    modifier = Modifier.align(Alignment.CenterVertically),
//                                    style = MaterialTheme.typography.bodyLarge
//                                )
//                            }
//                        }
//                    }

            item {
                OutlinedCard(
                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    border = BorderStroke(1.dp, Color.Black),
                    modifier = Modifier.size(390.dp, 200.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterHorizontally)) {

                        Text(
                            text = stringResource(id = R.string.MAIN_Pomoc_Linki),
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
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

//------------------------------------Główna-----------------------------------------


@Composable
fun HomeScreen(navController: NavHostController, padding: PaddingValues) {
    var selectedKey by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            selectedKey = (selectedKey + 1) % 4
            listState.animateScrollToItem(index = selectedKey)
        }
    }

//TODO("Dodać tło do HomeScreen")
//    Image(
//        painter = painterResource(id = R.drawable.lacznosc_logo_full_ia),
//        contentDescription = null,
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.LightGray) // Optional: Fallback color
//    )
    LazyColumn(
        Modifier
            .fillMaxHeight()
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
                if (SDK_INT <= Build.VERSION_CODES.R) {
                    item(key = 5) {
                        OutlinedCard(
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            border = BorderStroke(1.dp, Color.Black),
                            elevation = CardDefaults.elevatedCardElevation(10.dp),
                            modifier = Modifier
                                .fillParentMaxWidth(1f)
                                .wrapContentHeight()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Ta wersja Androida jest niebezpieczna!",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(text = "Android ${Build.VERSION.RELEASE} nie jest już wspierany przez Google i AOSP.\nJeśli jest to możliwe zaktualizuj swój smartfon.")
                            }
                        }
                    }
                }

                val assetManager = context.assets
                val inputStream = assetManager.open("Tipsu")
                val tips = BufferedReader(InputStreamReader(inputStream)).readLines()
                inputStream.close()


                items(5, key = { it }) {
                    val tip by remember { mutableStateOf(tips.random()) }
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
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(text = "Więcej funkcji już wkrótce")
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


//-----------------------------CO NOWEGO-----------------------------------------

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhatsNew(navController: NavHostController) {
    val context = LocalContext.current
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
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
    ) { padding ->
        val assetManager = context.assets
        val inputStream = assetManager.open("Whatsnew")
        val textofwhatsnew = inputStream.bufferedReader().use { it.readText() }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(horizontal = 10.dp),
            contentPadding = padding
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

//-----------------------------PLAN-----------------------------------------

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalComposeUiApi
@Composable
fun PlanScreen(context: Context, vibrator: Vibrator) {
    val accessdatastoremanager =
        Datastoremanager(context) //Zmień na jeden datastore dla całej aktywności

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
    var selectedChipOption = remember { mutableIntStateOf(1) }

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
            selectedChipOption.intValue = defultplan ?: 1
        }
    } //Domyślny chip na liście ModalBottomSheet przy pierwszym uruchomieniu

    LaunchedEffect(true) {
        accessdatastoremanager.getOnlineMode.collect { onlinemode ->
            online = onlinemode ?: false
        }
    } //Czy online  przy pierwszym uruchomieniu


    suspend fun loadscheduledata(): Schedule? {
        val datastore = Datastoremanager(context)

        var scheduleFromLoad: Schedule? = null

        try {
            val scheduleTimestamp = datastore.getPlanTimestamp.first()
            val favSchedule = datastore.getFavSchedule.first()
            onlysubstitute = datastore.getUserRefresh.first() == true

            if (selectedData2!!.isNotEmpty()) {
                klasa2 = selectedData!!.substring(0, 3)
                when (online) {
                    true -> {
                        Log.d(
                            "PlanScreen",
                            "loadscheduledata online (selecteddata not null)"
                        )
                        scheduleFromLoad = webscrapeT(
                            context,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/$selectedData2.html",
                            selectedData2.toString(),
                            LocalDate.now().toString(),
                            true
                        )
                    }

                    false -> {
                        Log.d(
                            "PlanScreen",
                            "loadscheduledata offline (selecteddata not null)"
                        )
                        scheduleFromLoad = datastore.getSchedule(
                            context,
                            "$scheduleTimestamp/$selectedData2",
                            1
                        )
                    }
                }
            } else {
                if (favSchedule!!.isNotEmpty()) {
                    selectedData = favSchedule.split(",")[0]
                    selectedData2 = favSchedule.split(",")[1]
                    when (online) {
                        true -> {
                            Log.d(
                                "PlanScreen",
                                "loadscheduledata online (favschedule not null) $selectedData, $selectedData2"
                            )
                            scheduleFromLoad = webscrapeT(
                                context,
                                "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/$selectedData2.html",
                                selectedData2.toString(),
                                LocalDate.now().toString(),
                                true
                            )
                        }

                        false -> {
                            Log.d(
                                "PlanScreen",
                                "loadscheduledata offline (favschedule not null)"
                            )
                            scheduleFromLoad = datastore.getSchedule(
                                context,
                                "$scheduleTimestamp/$selectedData2",
                                1
                            )
                        }
                    }
                } else {
                    scheduleFromLoad = null
                }
            }
        } catch (e: Exception) {
            // Handle errors here, e.g., log the error
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

    val options = listOf(
        stringResource(id = R.string.PLAN_Chip_Klasa),
        stringResource(id = R.string.PLAN_Chip_Sala),
        stringResource(id = R.string.PLAN_Chip_Nauczyciel)
    )

    val daynames = listOf(
        stringResource(id = R.string.PLAN_Button_Dropdownlist_Poniedziałek),
        stringResource(id = R.string.PLAN_Button_Dropdownlist_Wtorek),
        stringResource(id = R.string.PLAN_Button_Dropdownlist_Sroda),
        stringResource(id = R.string.PLAN_Button_Dropdownlist_Czwartek),
        stringResource(id = R.string.PLAN_Button_Dropdownlist_Piatek)
    )


    val result = RefreshWorker.DataHolder.workerResult.value
    LaunchedEffect(key1 = result) {
        if (result != null) {
            try {
                Log.d("itemid - scrolowanie", result.toString())
                listState.animateScrollToItem(index = result, scrollOffset = -600)
            } catch (_: Exception) {
                Toast.makeText(context, "Nie przeskrolowano, brak godziny", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    LaunchedEffect(refreshTrigger2) {
        sheetState.hide()
        showBottomSheet = false
    }

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
    }

    LaunchedEffect(selectedData, refreshTrigger, day) {
        isLoading = true
        isrefresing = true
        coroutineScope.launch(Dispatchers.IO) {
            listitems = loadsubstitue()
            isLoading = false
            isrefresing = false
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentPadding = PaddingValues(horizontal = 10.dp)
            ) {
                Text(
                    if (selectedData != "") {
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
                ) { Text(daytext) }

                Button(onClick = {
                    showBottomSheet = true
                }) { Text(stringResource(id = R.string.PLAN_Button_Zmień)) }

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

            HorizontalDivider(
                color = Color.Black,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
            )
        }
    ) { padding ->
        //Wyświetlane ekrany w zależności od opcji

        PullToRefreshBox(
            state = pullrefreshstate,
            isRefreshing = isrefresing,
            onRefresh = {
                isrefresing = true
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                coroutineScope.launch {
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
        ) {
            if (scheduleData != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = listState,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = padding
                ) {
                    scheduleData!!.plan.forEach {
                        val numerlekcji = it.numerLekcji
                        val czas = it.czas
                        val dzien = it.dzien
                        val nauczyciel = it.nauczyciel
                        val klasa = it.klasa
                        val przedmiot = it.przedmiot
                        val sala = it.sala

                        Log.d("PLANLOADING", "Zastępstwo: $listitems")

                        if (dzien == day.ordinal) {
                            item {
                                OutlinedCard(
                                    modifier = Modifier
                                        .height(130.dp)
                                        .padding(vertical = 5.dp)
                                        .fillParentMaxWidth(0.95f)
                                        .layoutId(numerlekcji),
                                    shape = MaterialTheme.shapes.medium,
                                    colors = if (numerlekcji == result && day == LocalDate.now().dayOfWeek) {
                                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                                    } else {
                                        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.fillParentMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                    ) {
                                        Text(text = sala)
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                    ) {
                                        Column(
                                            Modifier
                                                .fillMaxHeight()
                                                .fillMaxWidth(0.3f)
                                                .background(
                                                    if (numerlekcji == result && day == LocalDate.now().dayOfWeek) {
                                                        MaterialTheme.colorScheme.surfaceContainerHighest
                                                    } else {
                                                        MaterialTheme.colorScheme.primaryContainer
                                                    }
                                                ),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = "$numerlekcji \n-\n $czas",
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                        Text(
                                            text = "$przedmiot \n $nauczyciel \n $klasa",
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth(1f)
                                                .align(Alignment.CenterVertically)
                                        )
                                    }
                                }

                                if (listitems != null && przedmiot != "" && dzien == day.ordinal) {
                                    listitems!!.forEach { zastdata2 ->
                                        if (numerlekcji == zastdata2.numerLekcji) {
                                            OutlinedCard(
                                                modifier = Modifier
                                                    .padding(vertical = 5.dp)
                                                    .fillParentMaxWidth(0.95f)
                                                    .layoutId(numerlekcji),
                                                shape = MaterialTheme.shapes.medium,
                                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                            ) {
                                                Column(
                                                    verticalArrangement = Arrangement.SpaceEvenly,
                                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                                ) {
                                                    Text(
                                                        text = "⬆️",
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        text = zastdata2.klasa,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
                                                        text = zastdata2.zastepca,
                                                        textAlign = TextAlign.Center
                                                    )
                                                    Text(
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
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Wybierz plan",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
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
                modifier = Modifier
                    .statusBarsPadding()
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
                        planstamptext = accessdatastoremanager.getPlanTimestamp.first().toString()
                    }
                    if (planstamptext != "") {
                        Text(text = planstamptext)
                    } else {
                        Text(text = "Brak pobranego planu lekcji")
                    }
                }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    items(options) { option ->
                        FilterChip(
                            selected = when (selectedChipOption.intValue) {
                                1 -> option == options[0]
                                2 -> option == options[1]
                                3 -> option == options[2]
                                else -> {
                                    false
                                }
                            },
                            onClick = {
                                selectedChipOption.intValue = when (option) {
                                    options[0] -> 1
                                    options[1] -> 2
                                    options[2] -> 3
                                    else -> {
                                        1
                                    }
                                }
                            },
                            label = { Text(option) },
                            leadingIcon = {
                                val imageVector = when (option) {
                                    stringResource(id = R.string.PLAN_Chip_Klasa) -> Icons.Filled.Star
                                    stringResource(id = R.string.PLAN_Chip_Sala) -> Icons.Filled.LocationOn
                                    stringResource(id = R.string.PLAN_Chip_Nauczyciel) -> Icons.Filled.AccountCircle
                                    else -> null
                                }
                                if (imageVector != null) {
                                    Icon(imageVector = imageVector, contentDescription = "Option")
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
}