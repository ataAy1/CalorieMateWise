package com.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("tr", "TR"))

    fun formatCurrentDateWithMonthName(): String {
        val currentDate = Date()
        val dateFormatWithMonthName = SimpleDateFormat("dd MMMM yyyy", Locale("tr", "TR"))
        return dateFormatWithMonthName.format(currentDate)
    }


    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}
