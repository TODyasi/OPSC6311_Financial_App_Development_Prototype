package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {
    private lateinit var goToAddCategories : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        val username = intent.getStringExtra("username") ?: ""


        goToAddCategories = findViewById(R.id.goToAddCategoryBtn)
        goToAddCategories.setOnClickListener{
            val intent = Intent(this, AddCategoryActivity::class.java )
            intent.putExtra("username", username)
            startActivity(intent)
        }

    }
}