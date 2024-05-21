package es.usj.individualassessment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.Comparators.CalendarComparator
import es.usj.individualassessment.Classes.Comparators.CityComparator
import es.usj.individualassessment.Classes.ListCities
import es.usj.individualassessment.databinding.ActivityCityListBinding
import es.usj.individualassessment.databinding.ListItemBinding
import java.util.Calendar
import java.util.Date


class CityList : AppCompatActivity() {

    private val view by lazy { ActivityCityListBinding.inflate(layoutInflater) }
    private lateinit var adapter: CityListAdapter

    private lateinit var listCities: MutableList<City>

    private var isOnlyFavourite = false
    private lateinit var originalCities : List<City>

    private val dayTheme = arrayOf(
        R.color.dark_blue,
        R.drawable.square_border_day,
        R.drawable.rounded_border_day,
        R.drawable.sunny,
    )

    private val nightTheme = arrayOf(
        R.color.blue,
        R.drawable.square_border_night,
        R.drawable.rounded_border_night,
        R.drawable.nighty
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isOnlyFavourite = intent.getBooleanExtra("show_only_favorites", false)

        // Modify the title based on the condition
        if (isOnlyFavourite) {
            supportActionBar?.title = "Favorite Cities"
        } else {
            supportActionBar?.title = "List Cities"
        }
        supportActionBar?.show()

        // Get the list of cities based on the condition
        originalCities = if (isOnlyFavourite) {
            ListCities.instance.filter { it.isFavourite }
        } else {
            ListCities.instance.clone() as ListCities
        }

        listCities = originalCities.toMutableList()


        // Create adapter with custom layout and set it to the ListView
        adapter = CityListAdapter(listCities)
        view.CitiesList.adapter = adapter


    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_address -> {
                sortCities(CityComparator()::compareAddress)
                true
            }
            R.id.sort_by_time -> {
                sortCities(CityComparator()::compareTime)
                true
            }
            R.id.sort_by_timezone -> {
                sortCities(CityComparator()::compareTimeZone)
                true
            }
            R.id.sort_by_weather -> {
                sortCities(CityComparator()::compareWeather)
                true
            }
            R.id.sort_by_is_day -> {
                sortCities(CityComparator()::compareIsDay)
                true
            }
            R.id.filter_by_country -> { showFilterDialog("Country"); return true }
            R.id.filter_by_city -> { showFilterDialog("City"); return true }
            R.id.filter_after -> { showFilterDialog("Time (After)"); return true }
            R.id.filter_before -> { showFilterDialog("Time (Before)"); return true }
            R.id.refresh -> {restartView(); return true }
            android.R.id.home -> {
                startActivity(Intent(this, MainMenu::class.java))
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun sortCities(comparator: (City, City) -> Int) {
        listCities.sortWith(comparator)
        updateView()
    }

    private fun restartView() {
        listCities.clear()
        listCities.addAll(originalCities)
        adapter.notifyDataSetChanged()
    }
    private fun updateView() {
        if(adapter.isEmpty) {
            val toast = Toast.makeText(applicationContext, "No city matches that filter", Toast.LENGTH_LONG)
            toast.setGravity(Gravity.CENTER, 0, 0) // Set toast position to center
            toast.show()

            restartView()
        }
        else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun showFilterDialog(filterType: String) {

        if (filterType.contains("Time")) {

            val currentTime = Calendar.getInstance()
            // Initialize the TimePickerDialog with the current time
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Create a Date instance to store the selected time
                    val selectedTime = Date().apply {
                        // Create a Calendar instance to set the selected hour and minute
                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }
                        // Set the time in the Date object
                        time = calendar.timeInMillis
                    }

                    listCities.removeIf {
                        val cal1 = it.calendar
                        val cal2 = cal1.clone() as Calendar
                        cal2.time = selectedTime

                        val comparation = CalendarComparator().compareTime(cal1, cal2)

                        when (filterType.split(" ")[1]) {
                            "(After)" -> comparation < 0
                            "(Before)" -> comparation > 0
                            else -> false
                        }
                    }

                    // Sort the cities and notify the adapter
                    sortCities(CityComparator()::compareTime)

                },
                currentTime.get(Calendar.HOUR_OF_DAY), // Initial hour value
                currentTime.get(Calendar.MINUTE), // Initial minute value
                true // Specify whether to show 24-hour time format
            )

            // Show the TimePickerDialog
            timePickerDialog.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter value for $filterType")

            val input = EditText(this)
            builder.setView(input)

            builder.setPositiveButton("OK") { _, _ ->
                val filterValue = input.text.toString()

                when (filterType) {
                    "Country" -> listCities.removeIf { !it.country.contains(filterValue) }
                    "City" -> listCities.removeIf { !it.name.contains(filterValue) }
                }
                // Notify adapter of data change
                updateView()
            }
            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }
    }


    inner class CityListAdapter(cities: List<City>) :
        ArrayAdapter<City>(this@CityList, R.layout.list_item, cities) {

        @SuppressLint("DiscouragedApi")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val binding: ListItemBinding
            val rowView: View

            if (convertView == null) {
                binding = ListItemBinding.inflate(LayoutInflater.from(context), parent, false)
                rowView = binding.root
                rowView.tag = binding
            } else {
                binding = convertView.tag as ListItemBinding
                rowView = convertView
            }

            val city = getItem(position) ?: throw Error("City not found")
            var name = city.name

            if(city.province != null) {
                name += ", ${city.province}"
            }

            binding.cityName.text = name
            binding.countryName.text = city.country
            binding.Time.text = city.getTimeString()

            Log.d("Icon", city.today.icon)
            val iconId = resources.getIdentifier(
                city.today.icon.replace("-", "_"), "drawable",
                packageName
            )
            binding.weather.setImageResource(iconId)

            // Set the fav icon and add the onClick listener to update it
            updateFavourite(binding, city)
            binding.favourite.setOnClickListener {
                city.isFavourite = !city.isFavourite
                updateFavourite(binding, city)
            }


            val (color, squareBorder, roundedBorder, background) = if (city.isDay) {
                dayTheme
            } else {
                nightTheme
            }

            binding.relativelayout.setBackgroundResource(background)
            binding.cityName.setBackgroundResource(roundedBorder)
            binding.countryName.setBackgroundResource(roundedBorder)
            binding.Time.setBackgroundResource(squareBorder)
            binding.favourite.setBackgroundResource(squareBorder)

            val mycolor = ContextCompat.getColor(context, color)

            binding.cityName.setTextColor(mycolor)
            binding.countryName.setTextColor(mycolor)
            binding.Time.setTextColor(mycolor)

            // Set the click listener to start DetailedCity activity
            rowView.setOnClickListener {
                val intent = Intent(context, DetailedCity::class.java)
                val index = ListCities.instance.indexOf(city)
                intent.putExtra("CITY_INDEX", index)
                context.startActivity(intent)
            }

            return rowView
        }
        private fun updateFavourite(binding: ListItemBinding, city : City) {
            if(city.isFavourite) {
                binding.favourite.setImageResource(R.drawable.star_icon)
            }
            else {
                binding.favourite.setImageResource(R.drawable.starborder_icon)
            }
        }
    }
}
