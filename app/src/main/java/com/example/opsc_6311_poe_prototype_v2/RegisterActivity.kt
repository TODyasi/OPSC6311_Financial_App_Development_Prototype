package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var editFirstName: EditText
    private lateinit var editLastName: EditText
    private lateinit var editUserName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editEmail: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        editFirstName = findViewById(R.id.firstNameText)
        editLastName = findViewById(R.id.lastNameText)
        editUserName = findViewById(R.id.userNameText)
        editPassword = findViewById(R.id.passwordText)
        editEmail = findViewById(R.id.emailText)
        btnRegister = findViewById(R.id.registrationBtn)

        btnRegister.setOnClickListener {
            val firstName = editFirstName.text.toString().trim()
            val lastName = editLastName.text.toString().trim()
            val username = editUserName.text.toString().trim()
            val password = editPassword.text.toString().trim()
            val email = editEmail.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(firstName, lastName, password, username, email)
            val db = FirebaseDatabase.getInstance().getReference("users")

            db.child(username).setValue(user)
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
