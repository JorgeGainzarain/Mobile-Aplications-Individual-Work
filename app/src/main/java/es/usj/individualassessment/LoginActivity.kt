package es.usj.individualassessment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.User
import es.usj.individualassessment.Classes.ListCities
import es.usj.individualassessment.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val view by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var city: City
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(view.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Ensure FirebaseApp is initialized before using it
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }


        database = FirebaseDatabase.getInstance().reference
        auth = Firebase.auth

        getCityIndex()
        manageLoginDetails()

    }

    /* Get city details */
    private fun getCityIndex() {
        val cityIndex = intent.getIntExtra("CITY_INDEX", -1)
        if (cityIndex != -1) {
            city = ListCities.instance[cityIndex]
        } else {
            finish() // Close the activity if the index is invalid
        }
    }


    /* Manage user interactions with activity */
    private fun manageLoginDetails(){

        var username: String
        var password: String

        view.buttonLogin.setOnClickListener {
            username = view.tvLoginUsername.text.toString().trim()
            password = view.tvLoginPassword.text.toString().trim()
            login(username, password)
        }

        view.buttonRegister.setOnClickListener {
            username = view.tvLoginUsername.text.toString().trim()
            password = view.tvLoginPassword.text.toString().trim()
            register(username, password)
        }
    }

    /* Allow user login into the chat */
    private fun login(username: String, password: String){
        /*
        // Temporal auto login
        val user = User(username, "$username@gmail.com", password)
        User.setInstance(user) // Set the current user to this user
        showMessage("Login Successful")
        openChat(user)

        val user = users.find { it.userName == username && it.password == password}
        if(user != null){
            showMessage("Login Successful")
            openChat(user)
        } else{
            showMessage("Incorrect credentials")
        }
        */

        auth.signInWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val authUser = auth.currentUser
                    val user = User(authUser)
                    showMessage("Login Successful")
                    authUser?.let { openChat(user) }
                } else {
                    showMessage("Login Failed: ${task.exception?.message}")
                }
            }

    }

    /* Allow user registration into the chat */
    private fun register(username: String, password: String){
        auth.createUserWithEmailAndPassword("$username@example.com", password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val user = User(username, "$username@example.com")
                    firebaseUser?.let {
                        database.child("users").child(it.uid).setValue(user)
                        showMessage("Registration Successful")
                        openChat(user)
                    }
                } else {
                    showMessage("Registration Failed: ${task.exception?.message}")
                }
            }
    }

    private fun openChat(user: User) {
        User.setInstance(user)
        val intent = Intent(this, Chat::class.java)
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}