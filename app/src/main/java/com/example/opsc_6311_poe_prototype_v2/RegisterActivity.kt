package com.example.opsc_6311_poe_prototype_v2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
            val user = User(
                editFirstName.text.toString(),
                editLastName.text.toString(),
                editPassword.text.toString(),
                editUserName.text.toString(),
                editEmail.text.toString()

            )

            val db = FirebaseDatabase.getInstance()
            val ref = db.getReference("users")

            // Use username as unique key
            ref.child(user.userName).setValue(user)
        }
    }
}
