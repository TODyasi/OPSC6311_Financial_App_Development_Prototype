package com.example.opsc_6311_poe_prototype_v2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExpensesAdapter(private val expenses: List<Expenses>) :
    RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val descriptionText: TextView = itemView.findViewById(R.id.descriptionText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val dateText: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_expenses_layout, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.categoryText.text = expense.categoryName
        holder.descriptionText.text = expense.expenseDescription
        holder.amountText.text = "R %.2f".format(expense.expenseAmount)
        holder.dateText.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expense.date)
    }

    override fun getItemCount(): Int = expenses.size
}