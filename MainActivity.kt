package com.example.opsc_6311_poe_prototype_v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : ComponentActivity() {
    private lateinit var expenses: ArrayList<Expenses>
    private lateinit var adaptor: ExpenseAdaptor
    private lateinit var linearLayoutManager : LinearLayoutManager
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        expenses = arrayListOf(
            Expenses("Transport", -800.00),
            Expenses("Groceries", -1500.00),
            Expenses("Entertainment", -500.00)
        )

        recyclerView = findViewById(R.id.recyclerview)
        adaptor= ExpenseAdaptor(expenses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adaptor

    }
}

