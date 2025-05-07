package com.example.opsc_6311_poe_prototype_v2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

    //the main purpose of the Expense holder is to connect the textviews in the expense view to the values in the parameters of the expense data class
    //Context is the current state of the application.
class ExpenseAdaptor(private val expenses:ArrayList<Expenses>) :
    RecyclerView.Adapter<ExpenseAdaptor.ExpenseHolder>() {

    class ExpenseHolder(view: View) : RecyclerView.ViewHolder(view){
        //get the two fields needed to display the expense.
        val categoryName : TextView = view.findViewById(R.id.categoryNameTextView)
        val expenseAmount : TextView = view.findViewById(R.id.expenseAmountTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_expenses_layout,parent,false)
        return ExpenseHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseHolder, position: Int) {
        val expenses : Expenses = expenses[position]
        val context =  holder.expenseAmount.context

        //Set text color to green if expense amount is positive or negative
        if(expenses.expenseAmount >= 0){
              holder.expenseAmount.text = "+ R%.2f".format(expenses.expenseAmount)
            holder.expenseAmount.setTextColor(ContextCompat.getColor(context, R.color.purple_200))
        }else{
            holder.expenseAmount.text = "- R%.2f".format(Math.abs(expenses.expenseAmount))
            holder.expenseAmount.setTextColor(ContextCompat.getColor(context, R.color.teal_200))
        }
        holder.categoryName.text = expenses.categoryName
        holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    override fun getItemCount(): Int {
        return expenses.size
    }
}