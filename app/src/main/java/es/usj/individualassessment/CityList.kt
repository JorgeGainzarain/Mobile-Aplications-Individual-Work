package es.usj.individualassessment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.databinding.ActivityCityListBinding
import es.usj.individualassessment.databinding.ListItemBinding


class CityList : AppCompatActivity() {

    private val view by lazy { ActivityCityListBinding.inflate(layoutInflater) }

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

        // Create adapter with custom layout and set it to the ListView
        val adapter = CityListAdapter(listCities)
        view.CitiesList.adapter = adapter
    }

    inner class CityListAdapter(private val cities: List<City>) :
        ArrayAdapter<City>(this, R.layout.list_item, cities) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            //SETUP

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

            // VARS and VALS

            val city = getItem(position) ?: throw Error("City not found")
            var name = city.name

            // SET VALUES

            if(city.province != null) {
                name += ", ${city.province}"
            }

            binding.cityName.text = name

            binding.countryName.text = city.country
            binding.Time.text = city.getTimeString()

            val (color, squareBorder, roundedBorder, background) = if (city.isDay()) {
                dayTheme
            } else {
                nightTheme
            }

            // Debug logs
            //Log.d("CityListAdapter", "City: ${city.name}, isDay: ${city.isDay()}")

            binding.relativelayout.setBackgroundResource(background)

            binding.cityName.setBackgroundResource(roundedBorder)
            binding.countryName.setBackgroundResource(roundedBorder)

            binding.Time.setBackgroundResource(squareBorder)
            binding.favourite.setBackgroundResource(squareBorder)

            val mycolor = ContextCompat.getColor(context,color)

            binding.cityName.setTextColor(mycolor)
            binding.countryName.setTextColor(mycolor)
            binding.Time.setTextColor(mycolor)


            return rowView
        }

    }



}