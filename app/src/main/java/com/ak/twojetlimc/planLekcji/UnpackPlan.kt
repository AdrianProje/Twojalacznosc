package com.ak.twojetlimc.planLekcji

import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

fun webscrapeT(
    htmllink: String,
    filename: String
): Schedule? {
    val items = mutableListOf<ScheduleItem>()
    var i = 0
    var tytul: String

    Log.d("UnpackPlan", "Wczytywanie strony $htmllink")

    try {
        val htmlfile = Jsoup.connect(htmllink).get()

        tytul =
            htmlfile.getElementsByClass("tytulnapis").text()
        Log.d("UnpackPlan", "Tytuł $tytul")

        val trelements = htmlfile.getElementsByClass("tabela").first()!!.getElementsByTag("tr")

        trelements.forEach { tr ->
            val listitem = mutableListOf<Element>()

            tr.getElementsByTag("td").forEach { td ->
                val elements = td.getElementsByClass("l")
                if (elements.isNotEmpty()) listitem.addAll(elements) else Log.d(
                    "UnpackPlan - Brak td",
                    "Nie znaleziono td w elementach"
                )
            }

            val nr: Int = tr.getElementsByClass("nr").first()?.text()?.toInt() ?: 0
            val g = tr.getElementsByClass("g").first()?.text() ?: " "


            listitem.forEach { item ->
                if (i == 5) {
                    i = 0
                }

                val klasa = item.getElementsByClass("o").first()?.text() ?: ""
                val przedmiot = item.getElementsByClass("p").first()?.text() ?: ""
                val sala = item.getElementsByClass("s").first()?.text() ?: ""
                val nauczyciel = item.getElementsByClass("n").first()?.text() ?: ""

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

        Log.d("UnpackPlan", "$tytul $items")
        Log.d("UnpackPlan", "Przetworzono dane $htmllink")
    } catch (e: Exception) {
        Log.d("UnpackPlan - Błąd", "Błąd $e")
        return null
    }
    return Schedule(tytul, filename, items)
}