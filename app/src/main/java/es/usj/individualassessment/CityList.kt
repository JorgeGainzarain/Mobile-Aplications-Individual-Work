package es.usj.individualassessment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import es.usj.individualassessment.databinding.ActivityCityListBinding
import es.usj.individualassessment.databinding.ListItemBinding

class CityList : AppCompatActivity() {

    private val view by lazy { ActivityCityListBinding.inflate(layoutInflater) }

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

            val city = getItem(position)
            binding.cityName.text = "${city?.name}, ${city?.province ?: ""}"
            binding.countryName.text = city?.country ?: ""
            binding.Time.text = "00:00" // Set your time here
            binding.weather.setImageResource(R.drawable.sunny_icon) // Set your weather icon here

            return rowView
        }
    }
}
