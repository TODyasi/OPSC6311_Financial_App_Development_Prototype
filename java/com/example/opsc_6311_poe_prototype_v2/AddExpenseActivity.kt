package com.example.opsc_6311_poe_prototype_v2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {
    private lateinit var editExpenseAmount: EditText
    private lateinit var descriptionText: EditText
    private lateinit var dropdown: Spinner
    private lateinit var selectDateText: TextView
    private lateinit var addExpenseBtn: Button
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        editExpenseAmount = findViewById(R.id.expenseAmountText)
        descriptionText = findViewById(R.id.enterDescriptionText)
        dropdown = findViewById(R.id.categoriesDropdown)
        selectDateText = findViewById(R.id.datePickerText)
        addExpenseBtn = findViewById(R.id.submitNewExpenseEntry)

        // Initialize Spinner with dynamic categories
        loadCategories(username)

        selectDateText.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    this@AddExpenseActivity.selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
                    selectDateText.text = this@AddExpenseActivity.selectedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        addExpenseBtn.setOnClickListener {
            val amountText = editExpenseAmount.text.toString()
            val amount = amountText.toDoubleOrNull()
            val description = descriptionText.text.toString()
            val category = dropdown.selectedItem?.toString() ?: ""

            // Validation
            if (amount == null) {
                Toast.makeText(this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedDate.isEmpty()) {
                Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (category.isEmpty() || category == "Select Category") {
                Toast.makeText(this, "Please select a valid category.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = try {
                formatter.parse(selectedDate)
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } ?: return@setOnClickListener

            val expense = Expenses(
                categoryName = category,
                expenseAmount = amount,
                expenseDescription = description,
                date = parsedDate
            )

            val db = FirebaseDatabase.getInstance()
            val expensesRef = db.getReference("users/$username/Expenses")
            val budgetRef = db.getReference("users/$username/Budget")

            // Save expense
            expensesRef.push().setValue(expense)
                .addOnSuccessListener {
                    // Update budget
                    budgetRef.get().addOnSuccessListener { snapshot ->
                        val budget = snapshot.getValue(Budget::class.java)
                        if (budget == null) {
                            Toast.makeText(this, "Expense saved, but no budget found.", Toast.LENGTH_SHORT).show()
                            finish()
                            return@addOnSuccessListener
                        }
                        val newBalance = budget.balance - amount
                        budgetRef.child("balance").setValue(newBalance)
                        Toast.makeText(this, "Expense saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, "Expense saved, but failed to update budget: ${it.message}", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save expense: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadCategories(username: String) {
        val db = FirebaseDatabase.getInstance()
        val categoriesRef = db.getReference("users/$username/Categories")
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryNames = mutableListOf("Select Category")
                for (child in snapshot.children) {
                    val category = child.getValue(Catagories::class.java)
                    if (category != null) {
                        categoryNames.add(category.categoryName)
                    }
                }
                val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                dropdown.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddExpenseActivity, "Failed to load categories: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}