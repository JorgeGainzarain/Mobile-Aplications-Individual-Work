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


fun getLocalCalendar(tzOffset: Int, date: Date): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("GTM"))
    calendar.time = date
    calendar.add(Calendar.HOUR_OF_DAY, tzOffset)
    return calendar
}




