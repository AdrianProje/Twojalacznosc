package com.ak.twojetlimc.planLekcji

import android.content.Context
import android.util.Log
import it.skrape.core.htmlDocument
import it.skrape.selects.DocElement
import it.skrape.selects.html5.a
import it.skrape.selects.html5.span
import it.skrape.selects.html5.table
import it.skrape.selects.html5.td
import it.skrape.selects.html5.tr
import it.skrape.selects.text
import okio.use
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

fun webscrapeT(
    context: Context,
    htmllink: String,
    filename: String
): Schedule? {
    val items = mutableListOf<ScheduleItem>()
    var tytul = filename

    var i = 0

    Log.d("UnpackPlan", "Wczytywanie strony $htmllink")

    try {
        val url = URL(htmllink)
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        val htmlfile = File(context.cacheDir, "plan.html")
        connection.inputStream.use { input ->
            FileOutputStream(htmlfile).use { output ->
                input.copyTo(output)
            }
        }

        htmlDocument(htmlfile) {
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

        Log.d("UnpackPlan", "$tytul $items")
        Log.d("UnpackPlan", "Przetworzono dane $htmllink")
    } catch (e: Exception) {
        Log.d("UnpackPlan - Błąd", "Błąd $e")
        return null
    }
    return Schedule(tytul, filename, items)
}