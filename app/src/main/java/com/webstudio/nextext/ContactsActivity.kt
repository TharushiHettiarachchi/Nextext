package com.webstudio.nextext

import UsersAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ContactsActivity :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)
        val recyclerView = findViewById<RecyclerView>(R.id.usersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val usersList = documents.map { doc ->
                    User(
                        name = doc.getString("firstName")+" "+doc.getString("lastName") ?: "",
                        mobile = doc.getString("mobile") ?: "",
                        profileUrl = doc.getString("profileUrl") ?: ""
                    )

                }
                val adapter = UsersAdapter(usersList) { user ->

                    Toast.makeText(this, "Message ${user.name}", Toast.LENGTH_SHORT).show()

                }
                recyclerView.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
            }

    }

    fun goToHome(view : View) {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}