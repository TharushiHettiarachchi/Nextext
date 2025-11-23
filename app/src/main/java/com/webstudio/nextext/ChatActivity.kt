package com.webstudio.nextext

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ChatActivity : AppCompatActivity() {

    private lateinit var messageBox: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessagesAdapter
    private val messagesList = mutableListOf<Message>()

    private val db = FirebaseFirestore.getInstance()
    private lateinit var sender: String
    private lateinit var receiver: String

    private var sentListener: ListenerRegistration? = null
    private var receivedListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageBox = findViewById(R.id.messageBox)
        recyclerView = findViewById(R.id.recyclerViewMessages)
        val chatUserName = findViewById<TextView>(R.id.chatUserName)

        val userName = intent.getStringExtra("userName")
        val mobile = intent.getStringExtra("mobile")
        receiver = mobile ?: "unknown"
        chatUserName.text = userName ?: receiver

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        sender = sharedPref.getString("mobile", "unknown") ?: "unknown"

        adapter = MessagesAdapter(sender)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        listenForMessages()
    }

    fun sendMessage(view: View) {
        val msg = messageBox.text.toString().trim()
        if (msg.isEmpty()) {
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show()
            return
        }

        val messageData = hashMapOf(
            "sender" to sender,
            "receiver" to receiver,
            "message" to msg,
            "timestamp" to Timestamp.now()
        )

        db.collection("messages")
            .add(messageData)
            .addOnSuccessListener { messageBox.text.clear() }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        listenForMessages();
    }

    private fun listenForMessages() {

        sentListener = db.collection("messages")
            .whereEqualTo("sender", sender)
            .whereEqualTo("receiver", receiver)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                updateMessages(snapshots?.documents?.mapNotNull { it.toObject(Message::class.java) })
            }

        receivedListener = db.collection("messages")
            .whereEqualTo("sender", receiver)
            .whereEqualTo("receiver", sender)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener
                updateMessages(snapshots?.documents?.mapNotNull { it.toObject(Message::class.java) })
            }
    }

    private fun updateMessages(newMessages: List<Message>?) {
        if (newMessages == null) return


        newMessages.forEach { msg ->
            if (messagesList.none { it.timestamp == msg.timestamp && it.message == msg.message }) {
                messagesList.add(msg)
            }
        }


        messagesList.sortBy { it.timestamp?.toDate() }

        adapter.submitList(messagesList.toList())
        recyclerView.scrollToPosition(messagesList.size - 1)
    }

    override fun onDestroy() {
        super.onDestroy()
        sentListener?.remove()
        receivedListener?.remove()
    }
}
