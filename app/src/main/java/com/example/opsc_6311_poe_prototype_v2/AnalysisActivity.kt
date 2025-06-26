package com.example.opsc_6311_poe_prototype_v2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

import java.util.*

class AnalysisActivity : AppCompatActivity() {

    private lateinit var etFromDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var ivFromCal: ImageView
    private lateinit var ivToCal: ImageView
    private lateinit var btnFilter: Button
    private lateinit var lineChart: LineChart
    private lateinit var username: String
    private var fromDate: Date? = null
    private var toDate: Date? = null
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private val expenses = mutableListOf<Expenses>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze_screen)

        etFromDate = findViewById(R.id.etFromDate)
        etToDate = findViewById(R.id.etToDate)
        ivFromCal = findViewById(R.id.ivFromCal)
        ivToCal = findViewById(R.id.ivToCal)
        btnFilter = findViewById(R.id.btnFilter)
        lineChart = findViewById(R.id.lineChart)


        username = intent.getStringExtra("username") ?: run {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        findViewById<Button>(R.id.backButton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        fetchExpenses()

        ivFromCal.setOnClickListener { pickDate(true) }
        ivToCal.setOnClickListener { pickDate(false) }

        etFromDate.setOnClickListener { pickDate(true) }
        etToDate.setOnClickListener { pickDate(false) }

        btnFilter.setOnClickListener {
            if (fromDate != null && toDate != null) {
                filterExpenses()
            } else {
                Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchExpenses() {
        val db = FirebaseDatabase.getInstance()
        val expensesRef = db.getReference("users/$username/Expenses")

        expensesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                expenses.clear()

                for (child in snapshot.children) {
                    val categoryName = child.child("categoryName").getValue(String::class.java) ?: "Unknown"
                    val expenseAmount = child.child("expenseAmount").getValue(Double::class.java) ?: 0.0
                    val expenseDescription = child.child("expenseDescription").getValue(String::class.java) ?: ""
                    val dateString = child.child("date").getValue(String::class.java) ?: ""

                    if (dateString.isNotEmpty()) {
                        expenses.add(
                            Expenses(
                                categoryName = categoryName,
                                expenseAmount = expenseAmount,
                                expenseDescription = expenseDescription,
                                date = dateString
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AnalysisActivity, "Failed to load expenses.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterExpenses() {
        val filtered = expenses.filter { expense ->
            try {
                val expenseDate = sdf.parse(expense.date)
                expenseDate != null && expenseDate >= fromDate && expenseDate <= toDate
            } catch (e: Exception) {
                false
            }
        }.sortedBy { sdf.parse(it.date) }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "No expenses found in selected range.", Toast.LENGTH_SHORT).show()
            lineChart.clear()
            return
        }

        val entries = ArrayList<Entry>()
        filtered.forEachIndexed { index, expense ->
            entries.add(Entry(index.toFloat(), expense.expenseAmount.toFloat()))
        }

        val lineDataSet = LineDataSet(entries, "Expenses Over Time")
        lineDataSet.color = resources.getColor(R.color.black, theme)
        lineDataSet.valueTextColor = resources.getColor(R.color.black, theme)
        lineDataSet.setCircleColor(resources.getColor(R.color.black, theme))
        lineDataSet.lineWidth = 2f

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.valueFormatter = IndexAxisValueFormatter(filtered.map { it.date })

        lineChart.axisRight.isEnabled = false
        lineChart.description.text = "Expense Amount (R)"
        lineChart.invalidate()
    }


    private fun pickDate(isFrom: Boolean) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this,
            { _, year, month, day ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)
                if (isFrom) {
                    fromDate = selected.time
                    etFromDate.setText(sdf.format(fromDate!!))
                } else {
                    toDate = selected.time
                    etToDate.setText(sdf.format(toDate!!))
                }
            },
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
