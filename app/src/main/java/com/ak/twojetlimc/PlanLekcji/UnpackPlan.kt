package com.ak.twojetlimc.PlanLekcji

import android.content.Context
import android.util.Log
import com.ak.twojetlimc.komponenty.Datastoremanager
import it.skrape.core.htmlDocument
import it.skrape.fetcher.BrowserFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.html5.a
import it.skrape.selects.html5.span
import it.skrape.selects.html5.table
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import it.skrape.selects.text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

suspend fun webscrapeT(context: Context, htmllink: String, filename: String) {
    val datastoremanager = Datastoremanager(context)
    val items = mutableListOf<ScheduleItem>()
    var tytul = filename

    var i = 0

    Log.d("UnpackPlan", "Wczytywanie strony $filename")

    try {
        skrape(BrowserFetcher) {
            request {
                url = htmllink
                method = Method.GET
            }

            response {
                htmlDocument {
                    td {
                        withClass = "tytul"
                        tytul = span {
                            withClass = "tytulnapis"
                            findFirst { text }
                        }
                    }


                    table {
                        withClass = "tabela"
                        val scheduleDetailsTrs = tr { findAll { this } }

                        scheduleDetailsTrs.forEach {
                            val listitem = mutableListOf<DocElement>()
                            it.td {
                                try {
                                    withClass = "l"
                                    val elements = findAll { this }
                                    if (elements.isNotEmpty()) {
                                        listitem.addAll(elements)
                                    } else {
                                        Log.d("UnpackPlan - Brak td", "Nie znaleziono td w $it")
                                    }
                                } catch (e: Exception) {
                                    Log.d("UnpackPlan", "BŁĄD")
                                }
                            }

                            val nr: Int = it.td {
                                try {
                                    withClass = "nr"
                                    Integer.parseInt(findFirst { text })
                                } catch (e: Exception) {
                                    0
                                }
                            }

                            val g = it.td {
                                try {
                                    withClass = "g"
                                    findFirst { text }
                                } catch (e: Exception) {
                                    ""
                                }
                            }

                            listitem.forEach { item ->
                                if (i == 5) {
                                    i = 0
                                }


                                val klasa = item.a {
                                    try {
                                        withClass = "o"
                                        findAll { text }
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }


                                val przedmiot = item.span {
                                    try {
                                        withClass = "p"
                                        findAll { text }
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }

                                val sala = item.a {
                                    try {
                                        withClass = "s"
                                        findAll { text }
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }

                                val nauczyciel = item.a {
                                    try {
                                        withClass = "n"
                                        findAll { text }
                                    } catch (e: Exception) {
                                        ""
                                    }
                                }

                                items.add(
                                    ScheduleItem(
                                        nr,
                                        g,
                                        i,
                                        nauczyciel,
                                        klasa,
                                        przedmiot,
                                        sala
                                    )
                                )
                                i++
                            }
                        }
                    }
                }
            }
        }

        CoroutineScope(Dispatchers.IO).launch {

            val timestamp = LocalDate.now().toString()
            datastoremanager.savePlanTimestamp(timestamp)

            datastoremanager.storeSchedule(
                context,
                "$timestamp/$filename",
                1,
                Schedule(tytul, filename, items)
            )
            Log.d("UnpackPlan", "$tytul $items")
        }
        Log.d("UnpackPlan", "Przetworzono dane $htmllink")

    } catch (e: Exception) {
        Log.d("UnpackPlan - Błąd", "Błąd $filename")
    }
}