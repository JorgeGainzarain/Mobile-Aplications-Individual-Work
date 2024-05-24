package es.usj.individualassessment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.User
import es.usj.individualassessment.Classes.ListCities
import es.usj.individualassessment.databinding.ActivityLoginBinding
import java.io.File

class LoginActivity : AppCompatActivity() {

    private val view by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var city: City
    private val users = mutableListOf<User>()
    private val jsonFilePath = "userList.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(view.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //loadUsers(jsonFilePath)
        getCityIndex()
        manageLoginDetails()

    }

    /* Get city details */
    private fun getCityIndex(){
        // Get the city index from the intent
        val cityIndex = intent.getIntExtra("CITY_INDEX", -1)
        if (cityIndex != -1) {
            city = ListCities.instance[cityIndex]
        } else {
            // Handle the case where the index is invalid
            finish() // Close the activity if the index is invalid
        }
    }

    /* Manage user interactions with activity */
    private fun manageLoginDetails(){

        var username: String
        var password: String

        view.buttonLogin.setOnClickListener {
            username = view.tvLoginUsername.text.toString()
            password = view.tvLoginPassword.text.toString()
            login(username, password)
        }

        view.buttonRegister.setOnClickListener {
            username = view.tvLoginUsername.text.toString()
            password = view.tvLoginPassword.text.toString()
            register(username, password)
        }
    }

    /* Allow user login into the chat */
    private fun login(username: String, password: String){

        // Temporal auto login
        val user = User(username, "$username@gmail.com", password)
        User.setInstance(user) // Set the current user to this user
        showMessage("Login Successful")
        openChat(user)

        /*
        val user = users.find { it.userName == username && it.password == password}
        if(user != null){
            showMessage("Login Successful")
            openChat(user)
        } else{
            showMessage("Incorrect credentials")
        }
        */

    }

    /* Allow user registration into the chat */
    private fun register(username: String, password: String){
        val userExists = users.any { it.userName == username }
        if(userExists){
            showMessage("User already exists")
        } else {
            val user = User(username, username + "@example.com", password)
            users.add(user)
            saveUsersToFile(jsonFilePath)
            showMessage("Registration Success")
            openChat(user)
        }
    }

    /* Store users into the json file */
    private fun loadUsers(filePath: String){
        val file = File(filePath)
        if(file.exists()){
            val json = file.readText()
            val moshi = Moshi.Builder().build()
            val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, User::class.java)
            val jsonAdapter: JsonAdapter<List<User>> = moshi.adapter(listType)
            val loadedUsers = jsonAdapter.fromJson(json)
            if(loadedUsers != null) users.addAll(loadedUsers)
        }
    }

    private fun saveUsersToFile(filePath: String){
        val file = File(filePath)
        val moshi = Moshi.Builder().build()
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, User::class.java)
        val jsonAdapter: JsonAdapter<List<User>> = moshi.adapter(listType)
        val json = jsonAdapter.toJson(users)
        file.writeText(json)
    }

    private fun openChat(user: User){
        val intent = Intent(this, Chat::class.java)
        //intent.putExtra("CITY_INDEX", ListCities.instance.indexOf(city))
        //val bundle = Bundle().apply { putSerializable("USER_OBJECT", user) }
        //intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun showMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}