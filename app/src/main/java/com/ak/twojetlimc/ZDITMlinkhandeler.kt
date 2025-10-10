package com.ak.twojetlimc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.ui.theme.TwojaŁącznośćTheme
import com.ak.twojetlimc.zditm.Tablicaodjazow
import com.ak.twojetlimc.zditm.getthedeparturesdata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ZDITMlinkhandeler : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val datastoremanager = Datastoremanager(this)
            val context = LocalContext.current
            var refreshzditmcount by remember { mutableStateOf(0) }
            var isloadingzditm by remember { mutableStateOf(false) }

            var zditmdatatoshow by remember {
                mutableStateOf(
                    Tablicaodjazow(
                        "",
                        "",
                        emptyList(),
                        "",
                        ""
                    )
                )
            }


            val infiniteTransition = rememberInfiniteTransition(label = "infinite rotation")
            val rotationStateloading by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = if (isloadingzditm) 360f else 0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ), label = "rotation"
            )

            val number = intent!!.getStringExtra("zditmvalue")?.toIntOrNull()

            if (number == null) {
                finish()
            }

            TwojaŁącznośćTheme {
                LaunchedEffect(Unit, refreshzditmcount) {
                    while (true) {
                        isloadingzditm = true
                        withContext(Dispatchers.IO) {
                            try {
                                val data = getthedeparturesdata(number!!)
                                withContext(Dispatchers.Main) {
                                    zditmdatatoshow = data
                                }
                            } catch (_: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Błąd pobierania danych",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            }
                        }
                        isloadingzditm = false
                        delay(10000)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isalreadysaved = runBlocking {
                        val savedStops: List<Int> = datastoremanager.getzditmList().first()
                        savedStops.contains(number!!)
                    }
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .fillMaxWidth()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isalreadysaved) {
                            Text(
                                text = "Czy chcesz dodać ten przystanek do aplikacji?",
                                style = MaterialTheme.typography.titleLarge
                            )
                        } else {
                            Text(
                                text = "Ten przystanek jest już dodany",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Text("Przystanek: " + zditmdatatoshow.stop_name)
                        Text("Numer przystanku: " + zditmdatatoshow.stop_number)

                        LazyColumn(
                            modifier = Modifier
                                .padding(innerPadding)
                                .padding(horizontal = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Card(
                                    colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    border = BorderStroke(1.dp, Color.Black),
                                    modifier = Modifier.fillMaxWidth(0.95f)
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
                                    if (!zditmdatatoshow.departures.isEmpty()) {
                                        val realtime = zditmdatatoshow.departures.first().time_real
                                        val odjazd = when (realtime) {
                                            0 -> {
                                                "Na przystanku"
                                            }

                                            null -> {
                                                zditmdatatoshow.departures.first().time_scheduled.toString()
                                            }

                                            else -> {
                                                "Za: $realtime min."
                                            }
                                        }


                                        Row(
                                            modifier = Modifier
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
                                                        text = "Linia: " + zditmdatatoshow.departures.first().line_number + " (${zditmdatatoshow.departures.first().direction})",
                                                        modifier = Modifier.basicMarquee(
                                                            spacing = MarqueeSpacing(
                                                                10.dp
                                                            )
                                                        )
                                                    )
                                                    Text(
                                                        "| $odjazd",
                                                        modifier = Modifier
                                                            .padding(horizontal = 5.dp)
                                                            .basicMarquee(
                                                                spacing = MarqueeSpacing(
                                                                    10.dp
                                                                )
                                                            )
                                                            .weight(0.5f),
                                                        textAlign = TextAlign.Right
                                                    )
                                                }

                                                if (zditmdatatoshow.message != null) {
                                                    Text(
                                                        text = zditmdatatoshow.message.toString(),
                                                        modifier = Modifier.basicMarquee()
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(Modifier.weight(1f))
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            zditmdatatoshow.departures.take(5)
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
                                        }
                                    }
                                }
                            }
                        }
                        Row {
                            Button(
                                enabled = !isalreadysaved,
                                onClick = {
                                    runBlocking {
                                        datastoremanager.savezditmList(listOf(number!!))
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainScreen::class.java
                                            )
                                        )
                                        finishAfterTransition()
                                    }
                                }
                            ) {
                                Text("Zapisz i przejdź do aplikacji")
                            }
                            Button(
                                onClick = {
                                    finish()
                                }
                            ) {
                                Text("Anuluj i wyjdź")
                            }
                        }
                    }
                }
            }
        }
    }
}