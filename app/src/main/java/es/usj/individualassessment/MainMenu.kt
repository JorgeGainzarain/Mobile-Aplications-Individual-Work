package es.usj.individualassessment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import es.usj.individualassessment.databinding.ActivityMainMenuBinding

class MainMenu : AppCompatActivity() {

    private val view by lazy { ActivityMainMenuBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        view.textViewLocation.text = "Current Location: \nLatitude: $latitude \nLongitude: $longitude"
    }
}
