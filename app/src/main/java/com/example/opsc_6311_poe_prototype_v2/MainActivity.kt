package com.example.opsc_6311_poe_prototype_v2

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.activity.ComponentActivity
import com.google.firebase.database.*

class MainActivity : ComponentActivity() {

    private lateinit var expensesLayout: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences
    private val expenses = arrayListOf<Expenses>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expensesLayout = findViewById(R.id.recentExpensesLayout)
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        findViewById<TextView>(R.id.userNameTextView).text = username

        fetchExpenses(username)

        findViewById<Button>(R.id.goToManageBudgetBtn).setOnClickListener {
            startActivity(Intent(this, ManageBudgetActivity::class.java).putExtra("username", username))
        }
        findViewById<Button>(R.id.goToAnalysisBtn).setOnClickListener {
            startActivity(Intent(this, AnalysisActivity::class.java).putExtra("username", username))
        }

        findViewById<Button>(R.id.goToCategoriesBtn).setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java).putExtra("username", username))
        }
        findViewById<Button>(R.id.goToHome).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).putExtra("username", username))
        }


        findViewById<Button>(R.id.logOutBtn).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes") { _, _ ->
                    sharedPreferences.edit().remove("username").apply()
                    finishAffinity()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun fetchExpenses(username: String) {
        val db = FirebaseDatabase.getInstance()
        val expensesRef = db.getReference("users/$username/Expenses")

        expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenses.clear()
                expensesLayout.removeAllViews()

                for (child in snapshot.children) {
                    val categoryName = child.child("categoryName").getValue(String::class.java) ?: "Unknown"
                    val expenseAmount = child.child("expenseAmount").getValue(Double::class.java) ?: 0.0
                    val expenseDescription = child.child("expenseDescription").getValue(String::class.java) ?: ""
                    val date = child.child("date").getValue(String::class.java) ?: ""

                    val expense = Expenses(
                        categoryName = categoryName,
                        expenseAmount = expenseAmount,
                        expenseDescription = expenseDescription,
                        date = date
                    )

                    expenses.add(expense)
                }

                expenses.sortByDescending { it.date }

                for (expense in expenses) {
                    addExpenseView(expense)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load expenses.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addExpenseView(expense: Expenses) {
        val expenseView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
            layoutParams = params
        }

        val descriptionView = TextView(this).apply {
            text = "Description: ${expense.expenseDescription}"
            textSize = 16f
            setTextColor(Color.parseColor("#000000"))
        }
        val categoryView = TextView(this).apply{
            text = "Category: ${expense.categoryName}"
            textSize = 16f
            setTextColor(Color.parseColor("#000000"))
        }


        val amountView = TextView(this).apply {
            text = "Amount: R${expense.expenseAmount}"
            textSize = 16f
            setTextColor(Color.parseColor("#000000"))
        }

        val dateView = TextView(this).apply {
            text = "Date: ${expense.date}"
            textSize = 14f
            setTextColor(Color.parseColor("#000000"))
        }


        expenseView.addView(descriptionView)
        expenseView.addView(amountView)
        expenseView.addView(categoryView)
        expenseView.addView(dateView)

        expensesLayout.addView(expenseView)
    }
}
