package com.example.daizcode

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.daizcode.databinding.ActivityMainBinding
import com.example.daizcode.ui.fragments.DashboardFragment
import com.example.daizcode.ui.fragments.PartsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Keep fragment instances so they survive tab switches
    private val dashboardFragment = DashboardFragment()
    private val partsFragment = PartsFragment()
    private var activeFragment: Fragment = dashboardFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            // Apply bottom padding to the fragment container, not the nav bar
            binding.fragmentContainer.setPadding(0, 0, 0, 0)
            insets
        }

        setupFragments()
        setupBottomNav()
    }

    private fun setupFragments() {
        // Add both fragments, hide the inactive one
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, partsFragment, "parts")
            .hide(partsFragment)
            .add(R.id.fragmentContainer, dashboardFragment, "dashboard")
            .commit()
    }

    private fun setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    switchFragment(dashboardFragment)
                    true
                }
                R.id.nav_parts -> {
                    switchFragment(partsFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(target: Fragment) {
        if (target == activeFragment) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment)
            .show(target)
            .commit()
        activeFragment = target
    }
}
