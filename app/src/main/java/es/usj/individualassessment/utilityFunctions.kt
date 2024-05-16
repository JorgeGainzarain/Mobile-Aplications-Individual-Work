package es.usj.individualassessment

import android.content.Context
import android.util.Log
import es.usj.individualassessment.Classes.City
import java.io.File
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

var listCities: MutableList<City> = mutableListOf()

fun getCities(context: Context): MutableList<City> {
    listCities = listOf<City>().toMutableList()
    val cities = mutableListOf<City>()
    val directory = File(context.filesDir, "cities")
    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        files?.forEach { file ->
            try {
                val jsonString = file.readText()
                val city = City(jsonString)
                cities.add(city)
                Log.d("CitiesDebug", "City ${city.name} Loaded succesfully")
            } catch (e: Exception) {
                Log.e("WeatherLoad", "Error reading file: ${file.name}", e)
            }
        }
    }
    Log.d("CitiesDebug", "Cities Loaded succesfully")
    return cities
}

/*
fun resetDir(dirName: String, context : Context) {
    // If directory already has files reset it
    val directory = File(context.filesDir, dirName)
    if (directory.exists()) {
        val files = directory.listFiles() ?: null
        if(!files.isNullOrEmpty()) {
            files.forEach { file -> file.delete() }
        }
    }
    else {
        directory.mkdirs()
    }
}

 */

/*
fun compareDate(date1: Date, date2: Date): Int {
    val cal1 = Calendar.getInstance()
    cal1.time = date1
    val year1 = cal1.get(Calendar.YEAR)
    val month1 = cal1.get(Calendar.MONTH)
    val day1 = cal1.get(Calendar.DAY_OF_MONTH)

    val cal2 = Calendar.getInstance()
    cal2.time = date2
    val year2 = cal2.get(Calendar.YEAR)
    val month2 = cal2.get(Calendar.MONTH)
    val day2 = cal2.get(Calendar.DAY_OF_MONTH)

    if (year1 < year2) {
        return -1
    } else if (year1 > year2) {
        return 1
    } else if (month1 < month2) {
        return -1
    } else if (month1 > month2) {
        return 1
    } else if (day1 < day2) {
        return -1
    } else if (day1 > day2) {
        return 1
    } else {
        return 0
    }
}


*/

/*
fun compareTime(cal1: Calendar, cal2: Calendar): Int {

    val hour1 = cal1.get(Calendar.HOUR_OF_DAY)
    val minute1 = cal1.get(Calendar.MINUTE)
    val second1 = cal1.get(Calendar.SECOND)


    val hour2 = cal2.get(Calendar.HOUR_OF_DAY)
    val minute2 = cal2.get(Calendar.MINUTE)
    val second2 = cal2.get(Calendar.SECOND)

    if (hour1 < hour2) {
        return -1
    } else if (hour1 > hour2) {
        return 1
    } else if (minute1 < minute2) {
        return -1
    } else if (minute1 > minute2) {
        return 1
    } else if (second1 < second2) {
        return -1
    } else if (second1 > second2) {
        return 1
    } else {
        return 0
    }
}
 */

fun getLocalCalendar(tzOffset: Int, date: Date): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GTM"))
    calendar.time = date
    calendar.add(Calendar.HOUR_OF_DAY, tzOffset)
    return calendar
}




