package com.ak.twojetlimc.komponenty

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ak.twojetlimc.planLekcji.webscrapeT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ClassPullRefresh(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        return try {
            //Wykonywanie działania
            val datastoremanager = Datastoremanager(applicationContext)

            runBlocking {
                val timestamp = datastoremanager.getPlanTimestamp.first().toString()
                launch(Dispatchers.IO) {
                    for (i in 1..30) {
                        webscrapeT(
                            applicationContext,
                            "https://www.tlimc.szczecin.pl/dzialy/plan_lekcji/_aktualny/plany/o$i.html",
                            "o$i",
                            timestamp,
                            false
                        )
                    }
                }

                return@runBlocking Result.success()
            }
        } catch (e: Exception) {
            Log.d("PlanCheck", e.toString())
            return Result.failure()
        }
    }
}