package com.ak.twojetlimc.komponenty

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.zastepstwa.webscrapeZT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ZasCheck(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        return try {
            Log.d("ZasCheck", "Pobieranie danych zastepstw")
            //Wykonywanie działania

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    for (i in 0..4) {
                        val nextdays = LocalDate.now().plusDays(i.toLong())
                        Log.d("ZasCheck", nextdays.toString())
                        webscrapeZT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/zastepstwa/index.php?info=dokumenty/$nextdays.html",
                            nextdays.toString()
                        )
                    }
                } catch (_: java.io.FileNotFoundException) {
                    Log.d("ZasCheck", "Błąd")
                }
            }

            Log.d("ZasCheck", "Zakończono")
            return Result.success()
        } catch (_: Exception) {
            Log.d("ZasCheck", "Wystąpił błąd")
            return Result.failure()
        }
    }
}