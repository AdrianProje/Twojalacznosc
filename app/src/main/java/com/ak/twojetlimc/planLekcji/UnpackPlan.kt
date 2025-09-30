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

        val trelements =
            htmlfile.getElementsByClass("tabela").first()!!.getElementsByTag("tr").toList()

        val valueofhtml = htmllink.substringAfterLast('/').substringBefore(".html")

        trelements.drop(1).forEach { tr ->
            val listitem = mutableListOf<Element>()

            tr.getElementsByTag("td").forEach { td ->
                val elements = td.getElementsByClass("l")
                listitem.addAll(elements)
            }

            val nr: Int = tr.getElementsByClass("nr").first()!!.wholeText().toInt() ?: 0

            val g = tr.getElementsByClass("g").first()?.wholeText().toString() ?: ""

            listitem.forEach { item ->
                if (i == 5) {
                    i = 0
                }

                val przedmiot = mutableListOf<String>()
                val sala = mutableListOf<String>()
                val nauczyciel = mutableListOf<String>()

                val detailsList = mutableListOf<ScheduleItemDetails>()

                val klasa = if (!valueofhtml.contains("o")) {
                    item.getElementsByClass("o").first()?.wholeText() ?: ""
                } else {
                    " "
                }


                item.getElementsByClass("p")?.forEach { item ->
                    przedmiot.add(item.wholeText() ?: " ")
                } ?: " "

                if (!valueofhtml.contains("s")) {
                    item.getElementsByClass("s")?.forEach { item ->
                        sala.add(item.wholeText() ?: " ")
                    } ?: " "
                } else {
                    sala.add(" ")
                }

                if (!valueofhtml.contains("n")) {
                    item.getElementsByClass("n")?.forEach { item ->
                        nauczyciel.add(item.wholeText() ?: " ")
                    } ?: ""
                } else {
                    nauczyciel.add(" ")
                }

                for (i in 0 until przedmiot.size) {
                    detailsList.add(
                        ScheduleItemDetails(
                            nauczyciel.getOrElse(i) { " " },
                            przedmiot.getOrElse(i) { " " },
                            sala.getOrElse(i) { " " }
                        )
                    )
                }

                items.add(
                    ScheduleItem(
                        nr,
                        g,
                        i,
                        klasa,
                        detailsList
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