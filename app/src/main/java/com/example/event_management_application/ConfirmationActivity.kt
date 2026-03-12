package com.example.event_management_application

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmation)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvPhone = findViewById<TextView>(R.id.tvPhone)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvEventType = findViewById<TextView>(R.id.tvEventType)
        val tvEventDate = findViewById<TextView>(R.id.tvEventDate)
        val tvGender = findViewById<TextView>(R.id.tvGender)
        val ivProfileImage = findViewById<ImageView>(R.id.ivProfileImage)

        // Retrieve and display all intent extras
        tvFullName.text = "Full Name: ${intent.getStringExtra("fullName")}"
        tvPhone.text = "Phone Number: ${intent.getStringExtra("phone")}"
        tvEmail.text = "Email: ${intent.getStringExtra("email")}"
        tvEventType.text = "Event Type: ${intent.getStringExtra("eventType")}"
        tvEventDate.text = "Event Date: ${intent.getStringExtra("eventDate")}"
        tvGender.text = "Gender: ${intent.getStringExtra("gender")}"

        // Load selected image using URI string with Uri.parse()
        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            ivProfileImage.setImageURI(imageUri)
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // Navigate to MainActivity and clear the back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
