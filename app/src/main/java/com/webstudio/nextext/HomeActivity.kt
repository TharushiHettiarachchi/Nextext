package com.webstudio.nextext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        loadChatList()
    }

    fun goToContacts(view: View) {
        startActivity(Intent(this, ContactsActivity::class.java))
    }

    fun goToProfile(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun logOut(view: View) {
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun loadChatList() {
        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val currentUser = sharedPref.getString("mobile", "") ?: ""

        val db = FirebaseFirestore.getInstance()


        db.collection("messages")
            .whereIn("sender", listOf(currentUser))
            .get()
            .addOnSuccessListener { senderMsgs ->
                db.collection("messages")
                    .whereIn("receiver", listOf(currentUser))
                    .get()
                    .addOnSuccessListener { receiverMsgs ->
                        val allMsgs = mutableListOf<Message>()
                        allMsgs.addAll(senderMsgs.toObjects(Message::class.java))
                        allMsgs.addAll(receiverMsgs.toObjects(Message::class.java))

                        val chatMap = mutableMapOf<String, Message>()

                        for (msg in allMsgs) {
                            val otherUser = if (msg.sender == currentUser) msg.receiver else msg.sender
                            val existing = chatMap[otherUser]

                            if (existing == null ||
                                (msg.timestamp != null && existing.timestamp != null &&
                                        msg.timestamp!! > existing.timestamp!!)
                            ) {
                                chatMap[otherUser] = msg
                            }
                        }

                        val chatPreviews = mutableListOf<ChatPreview>()
                        var fetchedCount = 0

                        for ((mobile, msg) in chatMap) {

                            db.collection("users")
                                .whereEqualTo("mobile", mobile)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val userDoc = querySnapshot.documents.firstOrNull()
                                    val firstName = userDoc?.getString("firstName") ?: ""
                                    val lastName = userDoc?.getString("lastName") ?: ""
                                    val name = if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
                                        "$firstName $lastName"
                                    } else {
                                        mobile
                                    }

                                    val profileUrl = userDoc?.getString("profileImage")
                                    chatPreviews.add(
                                        ChatPreview(
                                            userName = name,
                                            mobile = mobile,
                                            lastMessage = msg.message ?: "",
                                            timestamp = msg.timestamp?.toDate(),
                                            profileImage = profileUrl
                                        )
                                    )



                                    fetchedCount++
                                    if (fetchedCount == chatMap.size) {
                                        setupRecycler(chatPreviews)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e("Firestore", "Error fetching user: ${e.message}")
                                    fetchedCount++
                                    if (fetchedCount == chatMap.size) {
                                        setupRecycler(chatPreviews)
                                    }
                                }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching messages: ${e.message}")
            }
    }



    private fun setupRecycler(chatPreviews: List<ChatPreview>) {
        val recycler = findViewById<RecyclerView>(R.id.chatListRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = ChatListAdapter(chatPreviews) { chat ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("userName", chat.userName)
            intent.putExtra("mobile", chat.mobile)
            startActivity(intent)
        }
    }
}
