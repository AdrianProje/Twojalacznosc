//package com.ak.twojetlimc.komponenty
//
//
//import android.appwidget.AppWidgetManager
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.widget.Toast
//
//class WidgetPinnedReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.action == AppWidgetManager.) {
//            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
//            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
//                // Widget was successfully pinned
//                Toast.makeText(context, "Widget added to home screen!", Toast.LENGTH_SHORT).show()
//                // You could potentially trigger an update or configuration for this new widgetId here
//            } else {
//                // Widget pinning was cancelled or failed
//                Toast.makeText(context, "Widget pinning cancelled.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}