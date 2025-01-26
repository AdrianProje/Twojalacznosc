package com.ak.twojetlimc.komponenty

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.PlanLekcji.webscrapeT
import com.ak.twojetlimc.R
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import kotlin.concurrent.thread

class PlanCheck(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "POBIERANIEPLANU")
            .setSmallIcon(R.drawable.lacznosc_logo_full)
            .setContentTitle("Pobieranie i przetwarzanie planu lekcji")
            .setContentText("Może to zająć chwilkę...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)


        notificationManager.notify(2, notification.build())

        return try {
            Log.d("PlanCheck", "Sprawdzanie dostępności nowszej wersji planu lekcji")

            //Wykonywanie działania
            val datastoremanager = Datastoremanager(applicationContext)

            val timestamp = LocalDate.now().toString()

            val jobs = listOf(
                thread(start = true) {
                    for (i in 1..30) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/o$i.html",
                            "o$i",
                            timestamp
                        )
                    }
                },


                thread(start = true) {
                    for (i in 1..70) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/n$i.html",
                            "n$i",
                            timestamp
                        )
                    }
                },

                thread(start = true) {
                    for (i in 1..50) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/s$i.html",
                            "s$i",
                            timestamp
                        )
                    }
                }
            )

            jobs.forEach { it.join() }


            runBlocking {
                datastoremanager.savePlanTimestamp(timestamp)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.d("PlanCheck", e.toString())
            return Result.failure()
        }
    }
}