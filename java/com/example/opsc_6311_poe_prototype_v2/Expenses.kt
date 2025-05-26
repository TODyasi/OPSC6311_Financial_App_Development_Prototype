package com.example.opsc_6311_poe_prototype_v2

import java.util.Date

data class Expenses(var categoryName: String ="",
                    var expenseAmount: Double  = 0.0,
                    var expenseDescription: String = "",
                    var date: Date = Date()){
}