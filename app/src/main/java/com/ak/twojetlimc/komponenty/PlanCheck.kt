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
import com.ak.twojetlimc.planLekcji.Schedule
import com.ak.twojetlimc.planLekcji.webscrapeT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PlanCheck(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)

        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val cancelPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            3456,
            Intent(applicationContext, StopNotification::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        var planlist = mutableListOf<Schedule>()

        val notification = NotificationCompat.Builder(applicationContext, "POBIERANIEPLANU")
            .setSmallIcon(R.drawable.lacznosc_logo_transparent)
            .setContentTitle("Pobieranie i przetwarzanie planu lekcji")
            .setContentText("Może to zająć chwilkę...")
            .setContentIntent(pendingIntent).setProgress(150, 0, false)
            .addAction(R.drawable.lacznosc_logo_transparent, "Zatrzymaj", cancelPendingIntent)


        notificationManager.notify(3, notification.build())

        return try {
            Log.d("PlanCheck", "Pobieranie danych planu lekcji")

            //Wykonywanie działania
            val datastoremanager = Datastoremanager(applicationContext)

            val timestamp = LocalDate.now().toString()

            var currentProgress = 0;
            var maxProgress = 150;

            runBlocking {
                withContext(Dispatchers.IO) {
                    for (i in 1..30) {
                        ++currentProgress
                        updateProgress(
                            currentProgress,
                            maxProgress,
                            notificationManager,
                            notification
                        )

                        val result = webscrapeT(
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/o$i.html",
                            "o$i"
                        )
                        if (result != null) planlist.add(result)
                    }
                }

                withContext(Dispatchers.IO) {
                    for (i in 1..70) {
                        ++currentProgress
                        updateProgress(
                            currentProgress,
                            maxProgress,
                            notificationManager,
                            notification
                        )
                        val result = webscrapeT(
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/n$i.html",
                            "n$i"
                        )

                        if (result != null) planlist.add(result)
                    }
                }

                withContext(Dispatchers.IO) {
                    for (i in 1..50) {
                        ++currentProgress
                        updateProgress(
                            currentProgress,
                            maxProgress,
                            notificationManager,
                            notification
                        )

                        val result = webscrapeT(
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/s$i.html",
                            "s$i"
                        )

                        if (result != null) planlist.add(result)
                    }
                }
            }

            if (!isStopped) {
                runBlocking {
                    datastoremanager.savePlanTimestamp(timestamp)
                    planlist.forEach { schedule ->
                        datastoremanager.storeSchedule(
                            applicationContext,
                            "$timestamp/${schedule.html}",
                            1,
                            schedule
                        )
                    }
                }
            } else {
                notificationManager.cancel(3)
                return Result.failure()
            }


            Log.d("PlanCheck", "Zakończono")
            notificationManager.cancel(3)
            return Result.success()
        } catch (e: Exception) {
            Log.d("PlanCheck", e.toString())
            notificationManager.cancel(3)
            return Result.failure()
        }
    }

    private fun updateProgress(
        currentProgress: Int,
        maxProgress: Int,
        notificationManager: NotificationManager,
        notificationBuilder: NotificationCompat.Builder
    ) {
        val notification = if (isStopped) {
            notificationBuilder.setProgress(maxProgress, currentProgress, true)
                .setContentTitle("Zatrzymywanie").setContentText("Zatrzymywanie pobierania")
                .clearActions().setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .build()
        } else if (currentProgress >= maxProgress - 25) {
            notificationBuilder.setProgress(maxProgress, currentProgress, true)
                .setContentTitle("Zapisywanie planu")
                .clearActions().setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .build()
        } else {
            notificationBuilder.setProgress(maxProgress, currentProgress, false)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .build()
        }

        notificationManager.notify(3, notification)

        setProgressAsync(
            workDataOf(
                "progress" to currentProgress,
                "max" to maxProgress
            )
        )
    }
}