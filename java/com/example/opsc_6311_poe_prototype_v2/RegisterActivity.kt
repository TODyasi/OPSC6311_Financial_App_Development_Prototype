package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    lateinit var editEmail: EditText
    lateinit var btnRegister: Button
    lateinit var editFirstName: EditText
    lateinit var editLastName: EditText
    lateinit var editPassword: EditText
    lateinit var editUserName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        editPassword = findViewById(R.id.passwordText)
        editFirstName = findViewById(R.id.firstNameText)
        editEmail = findViewById(R.id.emailText)
        editLastName = findViewById(R.id.lastNameText)
        editUserName = findViewById(R.id.userNameText)
        btnRegister = findViewById(R.id.registrationBtn)

        btnRegister.setOnClickListener {
            val firstName = editFirstName.text.toString().trim()
            val lastName = editLastName.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val userName = editUserName.text.toString().trim()
            val email = editEmail.text.toString().trim()

            // Validate that none of the fields are empty
            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || userName.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(firstName, lastName, password, userName, email)

            val db = FirebaseDatabase.getInstance()
            val ref = db.getReference("users")

            ref.child(user.userName).setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }
}
