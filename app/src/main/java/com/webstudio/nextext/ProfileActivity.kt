package com.webstudio.nextext

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProfileActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        Glide.with(this)
            .load(R.drawable.userlarge)
            .circleCrop()
            .into(findViewById(R.id.imageView5))

    }


    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    fun pickImage(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageUri = data.data


            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(findViewById(R.id.imageView5))
        }
    }


}