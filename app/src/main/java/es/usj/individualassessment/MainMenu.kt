package es.usj.individualassessment

import android.content.Intent
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
        val location = intent.getStringExtra("location")

        view.textViewLocation.text = "Current Location: \n" + longitude + "\n" + latitude

        // Set OnClickListener for the button to start CityList activity
        view.CityList.setOnClickListener {
            val intent = Intent(this, CityList::class.java)
            startActivity(intent)
        }
    }
}
