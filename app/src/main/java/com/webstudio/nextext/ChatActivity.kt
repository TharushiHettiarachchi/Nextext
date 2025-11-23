package com.webstudio.nextext

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ChatActivity:AppCompatActivity() {

    private lateinit var messageBox: EditText
    private val db = FirebaseFirestore.getInstance()

    private lateinit var sender: String
    private lateinit var reciever: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val userName = intent.getStringExtra("userName")
        val mobile = intent.getStringExtra("mobile")



        findViewById<TextView>(R.id.chatUserName).text = userName
        messageBox = findViewById(R.id.messageBox)


        reciever = mobile?: "Unknown"
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
         sender = sharedPref.getString("mobile", "unknown") ?: "unknown"

        findViewById<TextView>(R.id.chatUserName).text = reciever

    }

    fun sendMessage(view: View){
        val msg = messageBox.text.toString().trim()
        if (msg.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show()
            return
        }


        val messageData = hashMapOf(
            "sender" to sender,
            "receiver" to reciever,
            "message" to msg,
            "timestamp" to Date()
        )


        db.collection("messages")
            .add(messageData)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
                messageBox.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


}