package es.usj.individualassessment

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.usj.individualassessment.Classes.City
import es.usj.individualassessment.Classes.ListCities
import es.usj.individualassessment.Classes.Message
import es.usj.individualassessment.Classes.User
import es.usj.individualassessment.databinding.ActivityChatBinding
import es.usj.individualassessment.databinding.MessageItemBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class Chat : AppCompatActivity() {

    private val view by lazy { ActivityChatBinding.inflate(layoutInflater) }
    private lateinit var adapter: ChatAdapter

    private var database: DatabaseReference? = FirebaseDatabase.getInstance().getReference()
    private lateinit var cityRef: DatabaseReference

    private lateinit var messages: MutableList<Message>
    private lateinit var city: City

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(view.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val cityIndex = intent.getIntExtra("CITY_INDEX", -1)
        if (cityIndex != -1) {
            city = ListCities.instance[cityIndex]
        } else {
            // Handle the case where the index is invalid
            finish() // Close the activity if the index is invalid
        }

        city.loadcomentaries()

        messages = city.getcomentaries().toMutableList()

        // Create adapter with custom layout and set it to the ListView
        adapter = ChatAdapter(messages)
        view.listViewMessages.adapter = adapter


        view.btnSend.setOnClickListener{
            val msg = view.textInput.text
            val currTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            city.addComment(User.getInstance(), msg.toString(), currTime)
            view.textInput.text.clear()
        }

        cityRef = database!!.child("cities").child(city.id()).child("commentaries")
        // Set up the real-time listener
        cityRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {

                for (childSnapshot in dataSnapshot.getChildren()) {
                    Log.d(
                        "ComentaryChild2",
                        "Key: " + childSnapshot.key + ", Value: " + childSnapshot.value
                    )
                }

                if (dataSnapshot.exists()) {
                    val msg: String = dataSnapshot.child("message").getValue(
                        String::class.java
                    ).toString()
                    val time: String = dataSnapshot.child("time").getValue(
                        String::class.java
                    ).toString()

                    // Assuming user details are stored under a 'user' node
                    val username: String =
                        dataSnapshot.child("user").child("userName").getValue(
                            String::class.java
                        ).toString()
                    val mail: String =
                        dataSnapshot.child("user").child("userMail").getValue(
                            String::class.java
                        ).toString()
                    val color: String =
                        dataSnapshot.child("user").child("color").getValue(
                            String::class.java
                        ).toString()
                    val usr = User(username, mail, color)

                    val message = Message(usr, msg, time, dataSnapshot.key)

                    messages.add(message)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                // Get the unique ID of the removed message
                val removedMessageId = dataSnapshot.key

                // Find the corresponding message in the messages list and remove it
                val iterator = messages.iterator()
                while (iterator.hasNext()) {
                    val message = iterator.next()
                    if (message.id == removedMessageId) {
                        iterator.remove()
                        break // No need to continue searching once the message is found
                    }
                }

                // Notify adapter that data set has changed
                adapter.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Chat", "Failed to load messages.", databaseError.toException())
            }
        })

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

            // Load the drawable
            val drawable: Drawable? = ContextCompat.getDrawable(context, R.drawable.pfp_circle)


            // Modify the color of the bottom layer (circle background)
            if (drawable is LayerDrawable) {
                val bottomLayerIndex = 0 // Index of the bottom layer in the layer list
                val bottomLayer = drawable.getDrawable(bottomLayerIndex)
                if (bottomLayer is GradientDrawable) {
                    bottomLayer.setColor(Color.parseColor(user.color))
                }
            }

            // Set the modified drawable as the source of the ShapeableImageView
            binding.pfp.setImageDrawable(drawable)

            if (User.getInstance().userName.equals(user.userName)) {
                // If the current user is the same as the message user, show the delete button
                binding.delete.setVisibility(View.VISIBLE)
                binding.delete.setOnClickListener {
                    // Remove the message from the database

                    val message = messages[position]
                    val messageId = message.id

                    if (messageId != null) {
                        val cityRef = FirebaseDatabase.getInstance().getReference().child("cities").child(city.id())
                        val messageRef = cityRef.child("commentaries").child(messageId)
                        messageRef.removeValue()
                            .addOnSuccessListener {
                                // Message successfully removed from the database
                                Log.d("DeleteMessage", "Message removed successfully")
                            }
                            .addOnFailureListener { e ->
                                // Failed to remove message from the database
                                Log.e("DeleteMessage", "Failed to remove message", e)
                            }
                    } else {
                        Log.e("DeleteMessage", "Message ID is null")
                    }
                }
            } else {
                // If the current user is different, hide the delete button
                binding.delete.setVisibility(View.GONE)
            }


            return rowView
        }


    }

}
