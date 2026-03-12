package com.example.event_management_application

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerEventType: Spinner
    private lateinit var btnSelectDate: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var radioGroupGender: RadioGroup
    private lateinit var ivSelectedImage: ImageView
    private lateinit var btnChooseImage: Button
    private lateinit var cbTerms: CheckBox
    private lateinit var btnSubmit: Button

    private var selectedImageUri: Uri? = null
    private var selectedDate: String = ""
    private var isDateSelected = false
    private var selectedYear = 0
    private var selectedMonth = 0
    private var selectedDay = 0

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        // Initialize views
        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        spinnerEventType = findViewById(R.id.spinnerEventType)
        btnSelectDate = findViewById(R.id.btnSelectDate)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        radioGroupGender = findViewById(R.id.radioGroupGender)
        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        btnChooseImage = findViewById(R.id.btnChooseImage)
        cbTerms = findViewById(R.id.cbTerms)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Setup Spinner
        val eventTypes = arrayOf(
            "Select Event Type", "Seminar", "Workshop",
            "Conference", "Webinar", "Cultural Event"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, eventTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEventType.adapter = adapter

        // Setup DatePicker
        btnSelectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth
                    selectedDate = "$dayOfMonth/${month + 1}/$year"
                    tvSelectedDate.text = selectedDate
                    isDateSelected = true
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        // Setup Image Picker using ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                ivSelectedImage.setImageURI(selectedImageUri)
            }
        }

        btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        // Submit button
        btnSubmit.setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun validateAndSubmit() {
        val fullName = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val eventType = spinnerEventType.selectedItem.toString()
        val eventTypePosition = spinnerEventType.selectedItemPosition

        // Full Name validation
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }
        if (fullName.length < 3) {
            Toast.makeText(this, "Full name must be at least 3 characters", Toast.LENGTH_SHORT).show()
            return
        }
        if (fullName.any { it.isDigit() }) {
            Toast.makeText(this, "Full name must not contain digits", Toast.LENGTH_SHORT).show()
            return
        }

        // Phone validation
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter your phone number", Toast.LENGTH_SHORT).show()
            return
        }
        if (!phone.matches(Regex("^\\d{10,11}$"))) {
            Toast.makeText(this, "Phone number must be 10-11 digits", Toast.LENGTH_SHORT).show()
            return
        }

        // Email validation
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Spinner validation
        if (eventTypePosition == 0) {
            Toast.makeText(this, "Please select an event type", Toast.LENGTH_SHORT).show()
            return
        }

        // Date validation
        if (!isDateSelected) {
            Toast.makeText(this, "Please select an event date", Toast.LENGTH_SHORT).show()
            return
        }
        val today = Calendar.getInstance()
        val selected = Calendar.getInstance()
        selected.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
        selected.set(Calendar.MILLISECOND, 0)
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)
        if (selected.before(today)) {
            Toast.makeText(this, "Event date cannot be in the past", Toast.LENGTH_SHORT).show()
            return
        }

        // Gender validation
        val selectedGenderId = radioGroupGender.checkedRadioButtonId
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedGender = findViewById<RadioButton>(selectedGenderId).text.toString()

        // Image validation
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show()
            return
        }

        // Terms validation
        if (!cbTerms.isChecked) {
            Toast.makeText(this, "Please accept the Terms and Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // All validations passed — show confirmation dialog
        AlertDialog.Builder(this)
            .setTitle("Confirm Registration")
            .setMessage("Are you sure you want to submit your registration?")
            .setPositiveButton("Yes, Submit") { _, _ ->
                val intent = Intent(this, ConfirmationActivity::class.java)
                intent.putExtra("fullName", fullName)
                intent.putExtra("phone", phone)
                intent.putExtra("email", email)
                intent.putExtra("eventType", eventType)
                intent.putExtra("eventDate", selectedDate)
                intent.putExtra("gender", selectedGender)
                intent.putExtra("imageUri", selectedImageUri.toString())
                intent.clipData = ClipData.newRawUri("", selectedImageUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
