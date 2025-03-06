package com.ak.twojetlimc.AppWidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
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
import com.ak.twojetlimc.komponenty.Datastoremanager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class WidgetDumbUI : GlanceAppWidget() {
    override val stateDefinition = PreferencesGlanceStateDefinition
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            MyContent()
        }
    }
}

class WidgetActionUpdate : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        val datastore = Datastoremanager(context!!)
        val plantimestamp = runBlocking {
            datastore.getPlanTimestamp.first()
        }
        val fafsch = runBlocking {
            datastore.getFavSchedule.first()
        }

        if (plantimestamp != "" && fafsch != "") {

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}

@Composable
private fun MyContent() {
    Scaffold(
        backgroundColor = GlanceTheme.colors.widgetBackground
    ) {
        Column {
            Row(
                modifier = GlanceModifier.fillMaxWidth().background(GlanceTheme.colors.surface),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sala placeholder")
            }
            Row(
                modifier = GlanceModifier.fillMaxSize()
            ) {
                Text("Przedmiot placeholder")
            }
        }
    }
}