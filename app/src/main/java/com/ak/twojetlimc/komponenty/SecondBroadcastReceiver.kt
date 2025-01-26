package com.ak.twojetlimc.komponenty

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

class SecondBroadcastReceiver : android.content.BroadcastReceiver() {
    @SuppressLint("NewApi")
    override fun onReceive(context: Context, intent: Intent) {
        downloadplanandzas(context)

        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            createalarm(context)
        }
    }
}