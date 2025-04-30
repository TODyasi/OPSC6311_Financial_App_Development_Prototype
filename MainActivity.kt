package com.example.opsc6311_financial_app_development_prototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opsc6311_financial_app_development_prototype.ui.theme.OPSC6311_Financial_App_Development_PrototypeTheme

// 1. Expense Data Class to hold expense information
data class Expense(
    val category: String,
    val amount: Double
)

// 2. Main activity — entry point of your app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OPSC6311_Financial_App_Development_PrototypeTheme {
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

// 3. Welcome Container — shows welcome header and user icon
@Composable
fun WelcomeSectionContainer(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 36.dp, bottom = 16.dp) // Similar to Figma top/left
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Welcome Text Block
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp)
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 32.sp,
                    letterSpacing = 0.sp,
                    //fontFamily = FontFamily(Font(R.font.alkalami_regular)) // Uncomment if font is imported
                    color = Color(0xFF0C42CC)
                )
                Text(
                    text = userName,
                    fontSize = 18.sp,
                    color = Color(0xFF0C42CC)
                )
            }

            // User Icon
            Box(
                modifier = Modifier
                    .width(90.dp)
                    .height(105.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.placeholder_user_icon),
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.LightGray, shape = CircleShape)
                        .padding(8.dp),
                    tint = Color.Unspecified
                )
            }
        }
    }
}

// 4. Separator — horizontal blue line divider
@Composable
fun Separator() {
    Box(
        modifier = Modifier
            .width(412.dp)
            .height(7.dp)
            .background(Color(0xFF2196F3))
    )
}

fun String.hexToColor(): Color {
    return Color(android.graphics.Color.parseColor(this))
}
// 5. Categories Container — wraps the categories button
@Composable
fun CategoriesContainer(categoryCount: Int) {
    Card(
        modifier = Modifier
            .padding(top=45.dp)
            .width(387.dp)
            .height(100.dp),
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
                text = "$categoryCount", // Displays dynamic count
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}



// 6. Recent Expenses Container — wraps the section title and expense list
@Composable
fun RecentExpensesContainer(expenses: List<Expense>) {
    // Extract unique categories
    val categories = expenses.map { it.category }.distinct()

    Column {
        Text(
            text = "Recent Expenses",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        // Category Buttons
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                Button(
                    onClick = { /* Handle category click, e.g. filter expenses */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = category)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

    }
}

// 7. Dashboard Screen — main layout composed of all containers
@Composable
fun DashboardScreen(userName: String) {
    val expenses = listOf(
        Expense("Transport", 800.0),
        Expense("Groceries", 1500.0),
        Expense("Entertainment", 500.0)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        WelcomeSectionContainer(userName)
        Spacer(modifier = Modifier.height(16.dp))
        Separator()
        CategoriesContainer(categoryCount = 5)
        Spacer(modifier = Modifier.height(30.dp))
        Separator()
        RecentExpensesContainer(expenses = expenses)
    }
}

// 8. Preview Function — for testing in Android Studio
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OPSC6311_Financial_App_Development_PrototypeTheme {
        DashboardScreen(userName = "Zesande Mbekwa")
    }
}
