package com.ak.twojetlimc.komponenty

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.ak.twojetlimc.R
import com.ak.twojetlimc.planLekcji.webscrapeT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

class PlanCheck(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val cancelIntent = Intent(applicationContext, StopNotification::class.java)
        cancelIntent.action = "CANCEL_PLAN_CHECK"
        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            3456,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "POBIERANIEPLANU")
            .setSmallIcon(R.drawable.lacznosc_logo_transparent)
            .setContentTitle("Pobieranie i przetwarzanie planu lekcji")
            .setContentText("Może to zająć chwilkę...")
            .setContentIntent(pendingIntent).setProgress(150, 0, false)
            .addAction(R.drawable.lacznosc_logo_transparent, "Zatrzymaj", cancelPendingIntent)


        notificationManager.notify(2, notification.build())

        return try {
            Log.d("PlanCheck", "Pobieranie danych planu lekcji")

            //Wykonywanie działania
            val datastoremanager = Datastoremanager(applicationContext)

            val timestamp = LocalDate.now().toString()

            var currentProgress = 0;
            var maxProgress = 150;

            runBlocking {
                val jobs = listOf(
                    launch {
                        for (i in 1..30) {
                            ++currentProgress
                            updateProgress(
                                currentProgress,
                                maxProgress,
                                notificationManager,
                                notification
                            )
                            webscrapeT(
                                applicationContext,
                                "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/o$i.html",
                                "o$i",
                                timestamp,
                                false
                            )
                        }
                    },


                    launch(Dispatchers.IO) {
                        for (i in 1..70) {
                            ++currentProgress
                            updateProgress(
                                currentProgress,
                                maxProgress,
                                notificationManager,
                                notification
                            )
                            webscrapeT(
                                applicationContext,
                                "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/n$i.html",
                                "n$i",
                                timestamp,
                                false
                            )
                        }
                    },

                    launch {
                        for (i in 1..50) {
                            ++currentProgress
                            updateProgress(
                                currentProgress,
                                maxProgress,
                                notificationManager,
                                notification
                            )
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
            }

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

    private fun updateProgress(
        currentProgress: Int,
        maxProgress: Int,
        notificationManager: NotificationManager,
        notification: NotificationCompat.Builder
    ) {
        notification.setProgress(maxProgress, currentProgress, false)
        notificationManager.notify(2, notification.build())

        setProgressAsync(
            workDataOf(
                "progress" to currentProgress,
                "max" to maxProgress
            )
        )
    }
}