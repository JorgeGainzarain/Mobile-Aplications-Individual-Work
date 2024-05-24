package es.usj.individualassessment

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.DatabaseReference
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.Message
import es.usj.individualassessment.Classes.User
import es.usj.individualassessment.R
import es.usj.individualassessment.databinding.ActivityChatBinding
import es.usj.individualassessment.databinding.MessageItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import com.google.firebase.firestore.FirebaseFirestore

class Chat : AppCompatActivity() {

    private val view by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var adapter: ChatAdapter

    private lateinit var messages: MutableList<Message>
    private lateinit var city: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(view.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = FirebaseFirestore.getInstance()
        messages = mutableListOf()

        // Create adapter with custom layout and set it to the ListView
        adapter = ChatAdapter(messages)
        view.listViewMessages.adapter = adapter

        view.btnSend.setOnClickListener{
            val msg = view.textInput.text.toString()
            val currTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            val user = User.getInstance() // Get current user
            val newMessage = Message(user, msg, currTime)
            storeMessage(newMessage, db)
            // Update UI with new message (optional)
            messages.add(newMessage)
            adapter.notifyDataSetChanged()
        }

    }

    fun storeMessage(message: Message, db: FirebaseFirestore) {
        // Add a new document with a generated ID
        db.collection("messages")
            .add(message)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun retrieveMessages(db: FirebaseFirestore) {
        db.collection("messages")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val message = document.toObject(Message::class.java)
                    messages.add(message)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
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
