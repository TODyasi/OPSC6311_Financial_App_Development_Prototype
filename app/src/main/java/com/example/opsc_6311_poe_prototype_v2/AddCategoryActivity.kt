package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var addCategoryBtn: Button
    private lateinit var editCategoryName: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_add)

        val username = intent.getStringExtra("username")
        if (username == null) {
            finish()
            return
        }

        addCategoryBtn = findViewById(R.id.addCategoryBtn)
        editCategoryName = findViewById(R.id.categoryNameEditText)

        addCategoryBtn.setOnClickListener {
            val categoryName = editCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = FirebaseDatabase.getInstance()
            val categoriesRef = db.getReference("users/$username/Categories")

            // Check for duplicate category
            categoriesRef.orderByChild("categoryName").equalTo(categoryName)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "Category '$categoryName' already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        val category = Catagories(categoryName = categoryName)
                        categoriesRef.push().setValue(category)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Category added successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, CategoriesActivity::class.java)
                                intent.putExtra("username", username)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to add category: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to check category: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
