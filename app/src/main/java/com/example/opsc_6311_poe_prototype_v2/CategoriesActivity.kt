package com.example.opsc_6311_poe_prototype_v2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CategoriesActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_category_management)


        val goToAddCategory = findViewById<Button>(R.id.goToAddCategoryBtn)
        goToAddCategory.setOnClickListener{
            val intent = Intent(this, AddCategoryActivity::class.java )
            startActivity(intent)
        }

    }
}