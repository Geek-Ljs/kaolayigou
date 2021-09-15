package com.shoppingmail

import android.os.Build
import android.os.Bundle
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test.adapter.HomeAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shoppingmail.ui.object_class.DataBean
import com.spring.usekotlin.BannerImageAdapter
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.indicator.Indicator
import com.youth.banner.indicator.RectangleIndicator
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_recommend.*


class RecommendActivity : AppCompatActivity() {
    var show:Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()

        bottom_navigation.setLabelVisibilityMode(show)

        val navView : BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = findNavController(R.id.nav_recommend_fragment)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_recommend,
                R.id.navigation_message,
                R.id.navigation_cart,
                R.id.navigation_myself
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    fun showTitle():String{

        return "Title_Test"
    }
}
