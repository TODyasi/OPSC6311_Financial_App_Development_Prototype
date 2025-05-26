package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : ComponentActivity() {
    private lateinit var expenses: ArrayList<Expenses>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpensesAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Initialize RecyclerView
        expenses = arrayListOf()
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ExpensesAdapter(expenses)
        recyclerView.adapter = adapter

        // Fetch expenses from Firebase
        fetchExpenses(username)

        // Navigation buttons
        findViewById<Button>(R.id.goToManageBudgetBtn).setOnClickListener {
            val intent = Intent(this, ManageBudgetActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        findViewById<Button>(R.id.goToRegistrationBtn).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        findViewById<Button>(R.id.goToLoginBtn).setOnClickListener {
            sharedPreferences.edit().remove("username").apply()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.goToCategoriesBtn).setOnClickListener {
            val intent = Intent(this, CategoriesActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    private fun fetchExpenses(username: String) {
        val db = FirebaseDatabase.getInstance()
        val expensesRef = db.getReference("users/$username/Expenses")
        expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenses.clear()
                for (child in snapshot.children) {
                    val expense = child.getValue(Expenses::class.java)
                    if (expense != null) {
                        expenses.add(expense)
                    }
                }
                // Sort expenses by date (newest first)
                expenses.sortByDescending { it.date }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}