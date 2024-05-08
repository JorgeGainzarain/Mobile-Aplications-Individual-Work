package es.usj.individualassessment

import android.content.Context
import android.util.Log
import java.io.File

fun getCities(context: Context): List<City> {
    val cities = mutableListOf<City>()
    val directory = File(context.filesDir, "cities")
    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        files?.forEach { file ->
            try {
                val jsonString = file.readText()
                val city = City(jsonString)
                cities.add(city)
            } catch (e: Exception) {
                Log.e("WeatherLoad", "Error reading file: ${file.name}", e)
            }
        }
    }
    return cities
}
