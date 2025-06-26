package com.example.opsc_6311_poe_prototype_v2

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.lang.NumberFormatException

class AddMonthlyBudgetActivity : AppCompatActivity() {
    private lateinit var addMonthlyBudget: Button
    private lateinit var editMonthlyBudgetAmount: EditText
    private lateinit var categoryContainer: LinearLayout
    private lateinit var username: String
    private lateinit var addCategoryLimitBtn: Button
    private lateinit var addCategoryOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget_screen)

        username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        categoryContainer = findViewById(R.id.categoryDisplayContainer)
        addMonthlyBudget = findViewById(R.id.setBudgetBtn)
        editMonthlyBudgetAmount = findViewById(R.id.budgetAmountEditText)
        addCategoryLimitBtn = findViewById(R.id.addCategoryLimitBtn)
        addCategoryOverlay = findViewById(R.id.addCategoryOverlay)

        addCategoryLimitBtn.setOnClickListener {
            showCategoryOverlay()
        }

        addMonthlyBudget.setOnClickListener {
            val budgetAmount = editMonthlyBudgetAmount.text.toString()
            if (budgetAmount.isNotEmpty()) {
                try {
                    val budget = Budget(balance = budgetAmount.toDouble())
                    val db = FirebaseDatabase.getInstance()
                    val ref = db.getReference("users/$username/Budget")
                    ref.setValue(budget)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Budget set successfully", Toast.LENGTH_SHORT)
                                .show()
                            showCategoryOverlay()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                this,
                                "Failed to set budget: ${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
            }
        }
        fetchAndDisplayCategories()
    }
    private fun fetchAndDisplayCategories() {
        val db = FirebaseDatabase.getInstance()
        val categoriesRef = db.getReference("users/$username/Categories")

        categoriesRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                categoryContainer.removeAllViews()

                for (child in snapshot.children) {
                    val category = child.getValue(Catagories::class.java)
                    val categoryName = category?.categoryName ?: continue
                    addCategoryToLayout(categoryName)
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@AddMonthlyBudgetActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addCategoryToLayout(categoryName: String) {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.textfield_with_border)
        }

        val categoryNameTextView = TextView(this).apply {
            text = categoryName
            setTextColor(resources.getColor(R.color.black, null))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val categoryLimitTextView = TextView(this).apply {
            text = "Loading..."
            setTextColor(resources.getColor(R.color.black, null))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        layout.addView(categoryNameTextView)
        layout.addView(categoryLimitTextView)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 8, 0, 8)
        }

        categoryContainer.addView(layout, params)

        // Now fetch the limit for this category from Firebase
        val db = FirebaseDatabase.getInstance()
        val limitRef = db.getReference("users/$username/CategoriesLimits/$categoryName")

        limitRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val limit = snapshot.getValue(Double::class.java)
                categoryLimitTextView.text = if (limit != null) "Limit: R$limit" else "No limit set"
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                categoryLimitTextView.text = "Failed to load limit"
            }
        })

        // Optional click listener for the whole row (if needed)
        layout.setOnClickListener {
            Toast.makeText(this, "Clicked: $categoryName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showCategoryOverlay() {
        addCategoryOverlay.visibility = View.VISIBLE

        val categorySpinner = addCategoryOverlay.findViewById<Spinner>(R.id.categorySpinner)
        val limitEditText = addCategoryOverlay.findViewById<EditText>(R.id.categoryLimitEditText)
        val confirmBtn = addCategoryOverlay.findViewById<Button>(R.id.confirmLimitBtn)

        // Reset inputs on show
        limitEditText.text.clear()
        categorySpinner.setSelection(0)

        val db = FirebaseDatabase.getInstance()
        val categoriesRef = db.getReference("users/$username/Categories")

        categoriesRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val categoryNames = mutableListOf<String>()
                val categoryBudget = mutableListOf<String>()
                for (child in snapshot.children) {
                    val category = child.getValue(Catagories::class.java)
                    category?.categoryName?.let {
                        categoryNames.add(it)
                    }
                    category?.categoryBudget?.let {
                        categoryBudget.add(it.toString())
                    }
                }
                val adapter = ArrayAdapter(this@AddMonthlyBudgetActivity, android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                categorySpinner.adapter = adapter
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Toast.makeText(this@AddMonthlyBudgetActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
            }
        })

        confirmBtn.setOnClickListener {
            val selectedCategory = categorySpinner.selectedItem?.toString()
            val limitAmount = limitEditText.text.toString()

            if (selectedCategory.isNullOrEmpty() || limitAmount.isEmpty()) {
                Toast.makeText(this, "Please select a category and enter a limit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val limit = limitAmount.toDouble()
                confirmBtn.isEnabled = false  // disable while saving

                val limitRef = db.getReference("users/$username/CategoriesLimits/$selectedCategory")
                limitRef.setValue(limit)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Limit set for $selectedCategory", Toast.LENGTH_SHORT).show()
                        addCategoryOverlay.visibility = View.GONE
                        limitEditText.text.clear()
                        confirmBtn.isEnabled = true
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to set limit: ${it.message}", Toast.LENGTH_SHORT).show()
                        confirmBtn.isEnabled = true
                    }
            } catch (e: NumberFormatException) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }

}





