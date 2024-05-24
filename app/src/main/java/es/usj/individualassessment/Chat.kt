package es.usj.individualassessment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.Message
import es.usj.individualassessment.Classes.User
import es.usj.individualassessment.R
import es.usj.individualassessment.databinding.ActivityChatBinding
import es.usj.individualassessment.databinding.MessageItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Chat : AppCompatActivity() {

    private val view by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var adapter: ChatAdapter

    private lateinit var messages: MutableList<Message>
    private lateinit var city: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(view.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*
        val cityIndex = intent.getIntExtra("CITY_INDEX", -1)
        if (cityIndex != -1) {
            city = ListCities.instance[cityIndex]
        } else {
            // Handle the case where the index is invalid
            finish() // Close the activity if the index is invalid
        }
        */


        val user1 = User("Jorge","jorge@mail.com", "pass123")
        val user2 = User("Francisco", "francisco@gmail.com", "pass456")


        messages = mutableListOf()



        messages.add(Message(user1, "Hello!", "12:00"))
        messages.add(Message(user2, "Hi there!", "12:05"))


        // Create adapter with custom layout and set it to the ListView
        adapter = ChatAdapter(messages)
        view.listViewMessages.adapter = adapter


        view.btnSend.setOnClickListener{
            val msg = view.textInput.text
            val currTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            messages.add(Message(User.getInstance(), msg.toString(), currTime))
            adapter.notifyDataSetChanged()
        }

    }


    inner class ChatAdapter(messages: MutableList<Message>) :
        ArrayAdapter<Message>(this@Chat, R.layout.list_item, messages) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            val binding: MessageItemBinding
            val rowView: View

            if (convertView == null) {
                binding = MessageItemBinding.inflate(LayoutInflater.from(context), parent, false)
                rowView = binding.root
                rowView.tag = binding
            } else {
                binding = convertView.tag as MessageItemBinding
                rowView = convertView
            }

            val msg = getItem(position) ?: throw Error("Msg not found")
            val user = msg.user


            binding.userName.text = user.userName
            binding.hour.text = msg.time
            binding.messageText.text = msg.message


            return rowView
        }


    }

}
