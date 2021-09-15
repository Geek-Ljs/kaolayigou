package com.shoppingmail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.kaolahaigou.*

class kaolahaigou : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.kaolahaigou)
        supportActionBar?.hide()

        hot_back.setOnClickListener {
            finish()
        }
    }
}