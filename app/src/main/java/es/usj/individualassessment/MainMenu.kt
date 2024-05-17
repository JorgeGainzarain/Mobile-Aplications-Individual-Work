package es.usj.individualassessment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.usj.individualassessment.databinding.ActivityMainMenuBinding

class MainMenu : AppCompatActivity() {

    private val view by lazy { ActivityMainMenuBinding.inflate(layoutInflater) }
    companion object {
        var latitude: Double = 0.0
        var longitude: Double = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        supportActionBar?.hide()

        if(latitude == 0.0 && longitude == 0.0) {
            latitude = intent.getDoubleExtra("latitude", 0.0)
            longitude = intent.getDoubleExtra("longitude", 0.0)
        }


        view.textViewLocation.text = "Current Location: \nLat: " + "%.2f".format(latitude) + "    |    Long: " + "%.2f".format(longitude)

        // Set OnClickListener for the button to start CityList activity
        view.CityList.setOnClickListener {
            startActivity(Intent(this, CityList::class.java))
        }

        view.favCities.setOnClickListener{
            val intent = Intent(this, CityList::class.java)
            intent.putExtra("show_only_favorites", true)
            startActivity(intent)
        }

    }
}
