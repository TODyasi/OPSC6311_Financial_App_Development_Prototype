package com.example.opsc6311_financial_app_development_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opsc6311_financial_app_development_prototype.ui.theme.OPSC6311_Financial_App_Development_PrototypeTheme

// Expense Data Class
data class Expense(
    val category: String,
    val amount: Double
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OPSC6311_Financial_App_Development_PrototypeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen(userName = "Zesande Mbekwa")
                }
            }
        }
    }
}

// Categories Button Composable
@Composable
fun CategoriesButton(categoryCount: Int) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)) // Blue background
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "$categoryCount Categories", // Displays dynamic count
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

// Recent Expenses Composable
@Composable
fun RecentExpenses(expenses: List<Expense>) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(expenses) { expense ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.category,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = "R${expense.amount}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// Dashboard Screen Composable
@Composable
fun DashboardScreen(userName: String) {
    // Hardcoded list of recent expenses
    val expenses = listOf(
        Expense(category = "Transport", amount = 800.0),
        Expense(category = "Groceries", amount = 1500.0),
        Expense(category = "Entertainment", amount = 500.0)
    )

    Column(modifier = Modifier.fillMaxSize()) {

        // Welcome container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Welcome",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }

            // User icon
            Icon(
                painter = painterResource(id = R.drawable.placeholder_user_icon),
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(48.dp)
                    .background(color = Color.LightGray, shape = CircleShape)
                    .padding(8.dp),
                tint = Color.Unspecified
            )
        }

        // Blue separator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFF2196F3))
        )

        // Categories button (hardcoded to 5 categories for now)
        CategoriesButton(categoryCount = 5)

        // Recent Expenses section
        Text(
            text = "Recent Expenses",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Display the recent expenses
        RecentExpenses(expenses = expenses)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OPSC6311_Financial_App_Development_PrototypeTheme {
        DashboardScreen(userName = "Zesande Mbekwa")
    }
}
