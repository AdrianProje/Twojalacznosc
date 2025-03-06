package com.ak.twojetlimc.komponenty

import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

open class BroadcastReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workRequest = OneTimeWorkRequestBuilder<RefreshWorker>().build()
        val workRequest2 = OneTimeWorkRequestBuilder<ZasCheck>().build()

        WorkManager.getInstance(context)
            .beginWith(workRequest)
            .then(workRequest2)
            .enqueue()
    }
}