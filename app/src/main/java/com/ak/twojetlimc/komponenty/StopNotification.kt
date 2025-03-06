package com.ak.twojetlimc.komponenty

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager

class StopNotification : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("StopNotification", "StopNotification")
        WorkManager.getInstance(context).cancelAllWork()
    }
}