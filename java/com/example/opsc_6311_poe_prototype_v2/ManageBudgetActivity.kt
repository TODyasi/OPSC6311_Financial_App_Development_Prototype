package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ManageBudgetActivity : AppCompatActivity() {
    private lateinit var expenses: ArrayList<Expenses>
    private lateinit var budget: Budget
    private lateinit var addExpenseBtn: Button
    private lateinit var addBudgetBtn: Button
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_budget)

        // Get username from Intent
        username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Initialize default data
        expenses = arrayListOf()
        budget = Budget(0.0)

        // Set buttons
        addExpenseBtn = findViewById(R.id.addExpenseButton)
        addBudgetBtn = findViewById(R.id.setMonthlyBudgetButton)

        addExpenseBtn.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        addBudgetBtn.setOnClickListener {
            val intent = Intent(this, AddMonthlyBudgetActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Fetch and update user data
        fetchUserData()
    }

    private fun fetchUserData() {
        val db = FirebaseDatabase.getInstance()
        val budgetRef = db.getReference("users/$username/Budget")
        val expensesRef = db.getReference("users/$username/Expenses")

        // Fetch budget
        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseBudget = snapshot.getValue(Budget::class.java)
                budget = firebaseBudget ?: Budget(0.0)
                updateDashboard()
            }

            override fun onCancelled(error: DatabaseError) {
                updateDashboard()
            }
        })

        // Fetch expenses
        expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenses.clear()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expenses::class.java)
                    if (expense != null) {
                        expenses.add(expense)
                    }
                }
                updateDashboard()
            }

            override fun onCancelled(error: DatabaseError) {
                updateDashboard()
            }
        })
    }

    private fun updateDashboard() {
        val totalExpensesAmount = expenses.map { it.expenseAmount }.sum()
        val budgetAmount = budget.balance

        // Get references to the TextViews
        val totalPlannedText = findViewById<TextView>(R.id.totalPlannedExpenseAmountText)
        val totalBudgetText = findViewById<TextView>(R.id.totalBudgetAmountText)
        val monthlyBudgetSummary = findViewById<TextView>(R.id.monthlyBudgetSummary)

        // Update text values
        totalPlannedText.text = "R %.2f".format(totalExpensesAmount)
        totalBudgetText.text = "R %.2f".format(budgetAmount)
        monthlyBudgetSummary.text = "Current monthly budget: R %.2f".format(budgetAmount)
    }
}