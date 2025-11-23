package com.webstudio.nextext

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }
    fun goToContacts(view : View) {
        val intent = Intent(this, ContactsActivity::class.java)
        startActivity(intent)
    }
    fun goToProfile(view : View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }
    fun logOut(view : View) {

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        Toast.makeText(this,"Logged out successfully",Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}