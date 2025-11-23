package com.webstudio.nextext

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class ProfileActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)

        val fname = sharedPref.getString("firstName", "User Name")
        val lname = sharedPref.getString("lastName", "example@mail.com")
        val mobile = sharedPref.getString("mobile", "0000000000")

        findViewById<EditText>(R.id.profileFname).setText(fname)
        findViewById<EditText>(R.id.profileLname).setText(lname)
        findViewById<EditText>(R.id.profileMobile).setText(mobile)

        val savedImage = sharedPref.getString("profileImage", null)

        if (savedImage != null) {
            Glide.with(this)
                .load(savedImage)
                .circleCrop()
                .into(findViewById(R.id.imageView5))
        } else {
            Glide.with(this)
                .load(R.drawable.userlarge)
                .circleCrop()
                .into(findViewById(R.id.imageView5))
        }

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

    private fun uploadToCloudinary(imageUri: Uri, callback: (String?) -> Unit) {
        Thread {
            try {
                val inputStream = contentResolver.openInputStream(imageUri)
                val bytes = inputStream!!.readBytes()

                val requestBody = okhttp3.MultipartBody.Builder()
                    .setType(okhttp3.MultipartBody.FORM)
                    .addFormDataPart("file", "profile.jpg",
                        okhttp3.RequestBody.create(
                            "image/*".toMediaTypeOrNull(), bytes
                        )
                    )
                    .addFormDataPart("upload_preset", "ml_default")
                    .build()

                val request = okhttp3.Request.Builder()
                    .url("https://api.cloudinary.com/v1_1/dtguoyfdp/image/upload")
                    .post(requestBody)
                    .build()

                val client = okhttp3.OkHttpClient()
                val response = client.newCall(request).execute()

                val responseBody = response.body?.string()

                val imageUrl = org.json.JSONObject(responseBody).getString("secure_url")

                runOnUiThread { callback(imageUrl) }

            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { callback(null) }
            }
        }.start()
    }
    fun updateProfile(view: View) {

        val sharedPref = getSharedPreferences("UserData", MODE_PRIVATE)
        val mobile = sharedPref.getString("mobile", "") ?: return

        if (imageUri != null) {
            // Upload Image
            uploadToCloudinary(imageUri!!) { url ->
                if (url != null) {

                    // Save URL locally
                    sharedPref.edit().putString("profileImage", url).apply()

                    // Save to Firestore
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("users")
                        .whereEqualTo("mobile", mobile)
                        .get()
                        .addOnSuccessListener { docs ->
                            if (!docs.isEmpty) {
                                val docId = docs.documents[0].id

                                db.collection("users")
                                    .document(docId)
                                    .update("profileImage", url)

                                android.widget.Toast.makeText(this, "Profile updated!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    android.widget.Toast.makeText(this, "Upload Failed!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            android.widget.Toast.makeText(this, "No new image selected", android.widget.Toast.LENGTH_SHORT).show()
        }
    }


}