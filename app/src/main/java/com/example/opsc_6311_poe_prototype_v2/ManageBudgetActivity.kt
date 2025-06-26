package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.database.*
import android.animation.ObjectAnimator

class ManageBudgetActivity : AppCompatActivity() {

    private lateinit var expenses: ArrayList<Expenses>
    private lateinit var budget: Budget
    private lateinit var addExpenseBtn: ImageButton
    private lateinit var addBudgetBtn: ImageButton
    private lateinit var username: String
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_budget)

        username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        findViewById<Button>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        expenses = arrayListOf()
        budget = Budget(0.0)

        addExpenseBtn = findViewById(R.id.addExpenseButton)
        addBudgetBtn = findViewById(R.id.setMonthlyBudgetButton)
        pieChart = findViewById(R.id.budgetPieChart)

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

        fetchUserData()
    }

    private fun fetchUserData() {
        val db = FirebaseDatabase.getInstance()
        val budgetRef = db.getReference("users/$username/Budget")
        val expensesRef = db.getReference("users/$username/Expenses")

        budgetRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val firebaseBudget = snapshot.getValue(Budget::class.java)
                budget = firebaseBudget ?: Budget(0.0)
                updateDashboard()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

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
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun updateDashboard() {
        val totalExpensesAmount = expenses.sumOf { it.expenseAmount }
        val budgetAmount = budget.balance

        findViewById<TextView>(R.id.totalPlannedExpenseAmountText).text = "R %.2f".format(totalExpensesAmount)
        findViewById<TextView>(R.id.totalBudgetAmountText).text = "R %.2f".format(budgetAmount)

        updatePieChart(totalExpensesAmount, budgetAmount)
        updateProgressBar(totalExpensesAmount, budgetAmount)
    }

    private fun updatePieChart(expensesTotal: Double, budgetAmount: Double) {
        val remaining = (budgetAmount - expensesTotal).coerceAtLeast(0.0)

        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(expensesTotal.toFloat()))
        entries.add(PieEntry(remaining.toFloat()))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(Color.RED, Color.GREEN)
        dataSet.setDrawValues(false)

        pieChart.data = PieData(dataSet)
        pieChart.centerText = ""
        pieChart.invalidate()
    }
    private fun updateProgressBar(expensesTotal: Double, budgetAmount: Double) {
        val progressBar = findViewById<ProgressBar>(R.id.budgetProgressBar)
        val progressLabel = findViewById<TextView>(R.id.budgetProgressLabel)

        if (budgetAmount <= 0) {
            progressBar.progress = 0
            progressBar.progressDrawable.setTint(Color.GRAY)
            progressLabel.text = "No budget set"
            return
        }

        val percentage = ((expensesTotal / budgetAmount) * 100).coerceIn(0.0, 100.0)

        // Animate Progress Change
        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, percentage.toInt()).apply {
            duration = 800
            start()
        }

        // Dynamic Color
        when {
            percentage < 50 -> progressBar.progressDrawable.setTint(Color.parseColor("#4CAF50")) // Green
            percentage < 80 -> progressBar.progressDrawable.setTint(Color.parseColor("#FFC107")) // Orange
            else -> progressBar.progressDrawable.setTint(Color.parseColor("#F44336")) // Red
        }

        // Clear label for user
        progressLabel.text = "%.0f%% of budget used (R %.2f of R %.2f)".format(percentage, expensesTotal, budgetAmount)
    }

}
