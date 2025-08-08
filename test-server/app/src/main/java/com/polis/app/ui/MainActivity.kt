package com.polis.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.polis.app.R
import com.polis.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupBottomNavigation()
        
        // Start with Students by default
        startActivity(Intent(this, StudentListActivity::class.java))
    }
    
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_students -> {
                    startActivity(Intent(this, StudentListActivity::class.java))
                    true
                }
                R.id.nav_teachers -> {
                    startActivity(Intent(this, TeacherListActivity::class.java))
                    true
                }
                R.id.nav_courses -> {
                    startActivity(Intent(this, CourseListActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
} 