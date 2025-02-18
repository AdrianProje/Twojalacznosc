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
            .setSmallIcon(R.drawable.lacznosc_logo_transparent)
            .setContentTitle("Pobieranie i przetwarzanie planu lekcji")
            .setContentText("Może to zająć chwilkę...")
            .setContentIntent(pendingIntent)


        notificationManager.notify(2, notification.build())

        return try {
            Log.d("PlanCheck", "Pobieranie danych planu lekcji")

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
                            timestamp,
                            false
                        )
                    }
                },


                thread(start = true) {
                    for (i in 1..70) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/n$i.html",
                            "n$i",
                            timestamp,
                            false
                        )
                    }
                },

                thread(start = true) {
                    for (i in 1..50) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/s$i.html",
                            "s$i",
                            timestamp,
                            false
                        )
                    }
                }
            )

            jobs.forEach { it.join() }


            runBlocking {
                datastoremanager.savePlanTimestamp(timestamp)
            }

            Log.d("PlanCheck", "Zakończono")
            notificationManager.cancel(2)
            return Result.success()
        } catch (e: Exception) {
            Log.d("PlanCheck", e.toString())
            notificationManager.cancel(2)
            return Result.failure()
        }
    }
}