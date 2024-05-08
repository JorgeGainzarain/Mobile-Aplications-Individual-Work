package es.usj.individualassessment

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import es.usj.individualassessment.databinding.ActivityCityListBinding

class CityList : AppCompatActivity() {

    private val view by lazy { ActivityCityListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        // Sample data
        val cities = getCities(applicationContext)

        // Extract city names with province and country, separated by space and comma
        val cityNames = cities.map { "${it.name}, ${it.province}, ${it.country}" }

        // Create adapter and set it to the ListView
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cityNames)
        view.CitiesList.adapter = adapter
    }
}
