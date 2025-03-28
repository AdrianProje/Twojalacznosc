package com.ak.twojetlimc.AppWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.ak.twojetlimc.MainScreen
import com.ak.twojetlimc.R
import com.ak.twojetlimc.SettingsActivity
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.getcurrenthour
import com.ak.twojetlimc.planLekcji.Schedule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class WidgetDumbUI : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {

        provideContent {
            val datastore = Datastoremanager(context)
            val plantimestamp = runBlocking {
                datastore.getPlanTimestamp.first()
            }
            val fafsch = runBlocking {
                datastore.getFavSchedule.first()
            }.toString().split(",")[1]

            if (fafsch == "" || plantimestamp == "") {
                Scaffold(
                    backgroundColor = GlanceTheme.colors.background,
                    modifier = GlanceModifier.clickable(actionStartActivity(SettingsActivity::class.java))
                        .fillMaxSize()
                ) {
                    Row(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        Image(
                            ImageProvider(R.drawable.lacznosc_logo_full_ia),
                            "Logo aplikacji",
                            modifier = GlanceModifier.padding(horizontal = 10.dp)
                        )
                        Text("Brak danych klasy\n-\nUstaw domyślną klasę w ustawieniach aplikacji")
                    }
                }

            } else {
                val schedulefromload = runBlocking {
                    datastore.getSchedule(context, "$plantimestamp/$fafsch", 1)
                }
                MyContent(schedulefromload)
            }
        }
    }
}

class WidgetActionUpdate : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val scope = CoroutineScope(Dispatchers.IO)
        appWidgetIds.forEach { appWidgetId ->
            val glanceId =
                GlanceAppWidgetManager(context).getGlanceIdBy(appWidgetId)
            scope.launch {
                WidgetDumbUI().update(context, glanceId)
            }
        }
    }
}

@Composable
private fun MyContent(schedulefromload: Schedule?) {
    val currenthour = getcurrenthour()
    Scaffold(
        backgroundColor = GlanceTheme.colors.background,
        modifier = GlanceModifier.clickable(
            actionStartActivity(MainScreen::class.java)
        ).fillMaxSize()
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth()
        ) {
            Row(
                modifier = GlanceModifier.fillMaxWidth()
                    .background(GlanceTheme.colors.widgetBackground),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(schedulefromload!!.imieinazwisko)
            }

            schedulefromload!!.plan.forEach { item ->
                if (item.dzien + 1 == LocalDate.now().dayOfWeek.value && item.numerLekcji == currenthour) {
                    Log.d("WidgetDumbUi", item.toString())
                    Row(
                        modifier = GlanceModifier.fillMaxWidth()
                            .background(GlanceTheme.colors.widgetBackground),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(item.sala)
                    }
                    Row(
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = GlanceModifier.fillMaxHeight()
                                .background(GlanceTheme.colors.widgetBackground),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "${item.numerLekcji}\n-\n${item.czas}",
                                modifier = GlanceModifier.padding(horizontal = 10.dp)
                            )
                        }
                        Column(
                            modifier = GlanceModifier.fillMaxWidth().fillMaxHeight()
                                .defaultWeight(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(item.przedmiot)
                            Text(item.nauczyciel)
                        }
                    }
                }
            }
        }
    }
}