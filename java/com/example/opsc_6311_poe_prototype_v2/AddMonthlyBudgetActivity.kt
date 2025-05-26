package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.lang.NumberFormatException

class AddMonthlyBudgetActivity : AppCompatActivity() {
    private lateinit var addMonthlyBudget: Button
    private lateinit var editMonthlyBudgetAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_monthly_budget)

        val username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        addMonthlyBudget = findViewById(R.id.addMonthlyBudgetBtn)
        editMonthlyBudgetAmount = findViewById(R.id.budgetAmountText)

        addMonthlyBudget.setOnClickListener {
            val budgetAmount = editMonthlyBudgetAmount.text.toString()
            if (budgetAmount.isNotEmpty()) {
                try {
                    val budget = Budget(balance = budgetAmount.toDouble())
                    val db = FirebaseDatabase.getInstance()
                    val ref = db.getReference("users/$username/Budget")
                    ref.setValue(budget)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Budget set successfully", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, ManageBudgetActivity::class.java)
                            intent.putExtra("username", username)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to set budget: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}