package com.webstudio.nextext

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun goToCreateNewAccount(view : View) {
        val intent = Intent(this, CreateAccountActivity::class.java)
        startActivity(intent)
    }

    fun signIn(view: View) {
        val mobile = findViewById<EditText>(R.id.signInMobile).text.toString().trim()

        if (mobile.isEmpty()) {
            Toast.makeText(this, "Please enter your mobile number", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()


        db.collection("users")
            .whereEqualTo("mobile", mobile)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val userDoc = documents.documents[0]
                    val firstName = userDoc.getString("firstName")
                    val  lastName= userDoc.getString("lastName")
                    val mobileNumber = userDoc.getString("mobile")
                    val profileImage = userDoc.getString("profileImage")

                    val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
                    val editor = sharedPref.edit()

                    editor.putString("firstName", firstName)
                    editor.putString("lastName", lastName)
                    editor.putString("mobile", mobileNumber)
                    editor.putString("profileImage", profileImage)
                    editor.apply()
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {

                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



}