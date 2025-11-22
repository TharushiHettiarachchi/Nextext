package com.webstudio.nextext

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
    }

    fun goToSignIn(view : View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun createAccount(view : View){
        val fname = findViewById<EditText>(R.id.firstName).text.toString()
        val lname = findViewById<EditText>(R.id.lastName).text.toString()
        val mobile = findViewById<EditText>(R.id.newMobileNumber).text.toString()

        if (fname.isEmpty()) {
            Toast.makeText(this, "Please enter your First Name", Toast.LENGTH_SHORT).show()
            return
        }else  if (lname.isEmpty()) {
            Toast.makeText(this, "Please enter your Last Name", Toast.LENGTH_SHORT).show()
            return
        }else  if (mobile.isEmpty()) {
            Toast.makeText(this, "Please enter your Mobile Number", Toast.LENGTH_SHORT).show()
            return
        }else{
            val db = FirebaseFirestore.getInstance()

            val user = hashMapOf(
                "firstName" to fname,
                "lastName" to lname,
                "mobile" to mobile
            )

            db.collection("users")
                .add(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }



    }

}