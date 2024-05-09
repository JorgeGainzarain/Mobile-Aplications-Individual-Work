package es.usj.individualassessment

import android.content.Context
import android.util.Log
import java.io.File

var listCities = mutableListOf<City>() // Define listCities as a MutableList

fun getCities(context: Context): MutableList<City> {
    val cities = mutableListOf<City>()
    val directory = File(context.filesDir, "cities")
    if (directory.exists() && directory.isDirectory) {
        val files = directory.listFiles()
        files?.forEach { file ->
            try {
                val jsonString = file.readText()
                val city = City(jsonString)
                cities.add(city)
                listCities.add(city) // Add the city to the listCities
            } catch (e: Exception) {
                Log.e("WeatherLoad", "Error reading file: ${file.name}", e)
            }
        }
    }
    return cities
}

fun getProvince(address: String): String? {
    val parts = address.split(", ")
    return if (parts.size == 3) parts[1] else null
}

fun getCountry(address: String): String {
    val parts = address.split(", ")
    return if (parts.size == 3) parts.last() else parts[1]
}
