package com.ak.twojetlimc.komponenty

import android.annotation.SuppressLint
import android.content.res.AssetManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun findNewestFolder(assetManager: AssetManager): String? {
    val folderNames = assetManager.list("") ?: return null // Get all folder names in assets

    val dateFormat = SimpleDateFormat("yyyy_MM_dd") // Adjust the date format as needed

    var newestFolder: String? = null
    var newestDate: Date? = null

    for (folderName in folderNames) {
        val date = try {
            dateFormat.parse(folderName)
        } catch (e: Exception) {
            null
        }

        if (date != null && (newestDate == null || date.after(newestDate))) {
            newestFolder = folderName
            newestDate = date
        }
    }

    Log.d("NewestPlan - Znaleziono najnowszy folder", newestFolder.toString())
    return newestFolder
}