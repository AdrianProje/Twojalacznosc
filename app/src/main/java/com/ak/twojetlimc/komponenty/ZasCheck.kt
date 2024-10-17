package com.ak.twojetlimc.komponenty

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.R
import com.ak.twojetlimc.Zastepstwa.webscrapeZT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ZasCheck(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        return try {
            Log.d("ZasCheck", "Pobieranie danych")
            val notificationManager = NotificationManagerCompat.from(applicationContext)

            notificationManager.notify(2, createnotivication())

            //Wykonywanie działania

            //Wykonywanie działania
            val serviceScope = CoroutineScope(Dispatchers.IO)

            serviceScope.launch {
                try {
                    Log.d("ZasCheck", (LocalDate.now()).toString())
                    val currentday = LocalDate.now().toString()
                    webscrapeZT(
                        applicationContext,
                        "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/zastepstwa/index.php?info=dokumenty/$currentday.html",
                        currentday
                    )

                    Log.d("ZasCheck", (LocalDate.now().plusDays(1)).toString())
                    val nextday = LocalDate.now().plusDays(1).toString()
                    webscrapeZT(
                        applicationContext,
                        "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/zastepstwa/index.php?info=dokumenty/$nextday.html",
                        nextday
                    )

                } catch (e: java.io.FileNotFoundException) {
                    Log.d("ZasCheck", "Błąd - dzisiaj")

                }
            }

            notificationManager.cancel(2)
            return Result.success()
        } catch (e: Exception) {
            Log.d("ZasCheck", "Wystąpił błąd")
            return Result.failure()
        }
    }


    private fun createnotivication(): Notification {
        val notification = NotificationCompat.Builder(applicationContext, "POBIERANIEZASTEPSTW")
            .setSmallIcon(R.drawable.lacznosc_logo_full)
            .setContentTitle("Pobieranie i przetwarzanie zastępstw")
            .setContentText("Może to zająć chwilkę...")
            .setAutoCancel(true)
            .build()

        return notification
    }
}