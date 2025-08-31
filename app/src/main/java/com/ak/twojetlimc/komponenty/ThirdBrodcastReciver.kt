package com.ak.twojetlimc.komponenty

import android.content.Context
import android.content.Intent

class ThirdBrodcastReciver : android.content.BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        downloadonlyschedule(context)
    }
}

