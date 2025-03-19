package com.ak.twojetlimc.AppWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.ak.twojetlimc.MainScreen
import com.ak.twojetlimc.komponenty.Datastoremanager
import com.ak.twojetlimc.komponenty.getcurrenthour
import com.ak.twojetlimc.planLekcji.Schedule
import kotlinx.coroutines.flow.first
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

            if (fafsch != "" || plantimestamp != "") {
                Text("Brak danych")
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
    //    private val job = Job()
//    private val widgetScope = CoroutineScope(Dispatchers.Main + job)
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
//        widgetScope.launch {
//            appWidgetIds?.forEach { appWidgetId ->
//                WidgetDumbUI().update(context, appWidgetId)
//                Log.d("Widget", "Widget updated")
//            }
//        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}

@Composable
private fun MyContent(schedulefromload: Schedule?) {
    val currenthour = getcurrenthour()
    Scaffold(
        backgroundColor = GlanceTheme.colors.widgetBackground,
        modifier = GlanceModifier.clickable(actionStartActivity(MainScreen::class.java))
    ) {
        schedulefromload!!.plan.forEach { item ->
            if (item.dzien == LocalDate.now().dayOfWeek.value && item.numerLekcji == currenthour) {
                Column {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth()
                            .background(GlanceTheme.colors.surface),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${schedulefromload!!.imieinazwisko}")
                    }
                    Row(
                        modifier = GlanceModifier.fillMaxSize()
                    ) {
                        Text("Przedmiot ${item.przedmiot}")
                        Text("Numer lekcji ${item.numerLekcji}")
                        Text("Numer lekcji ${item.nauczyciel}")
                    }
                }
            }
        }
    }
}