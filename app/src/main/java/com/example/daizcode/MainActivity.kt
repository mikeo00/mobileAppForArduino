package com.example.daizcode

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daizcode.databinding.LayoutDashboardBinding
import com.example.daizcode.ui.adapters.DetectionAdapter
import com.example.daizcode.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: LayoutDashboardBinding
    private val viewModel: DashboardViewModel by viewModels()
    private val adapter = DetectionAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = LayoutDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupStatCards()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupStatCards() {
        // Total Card
        binding.cardTotal.statTitle.text = "TOTAL"
        binding.cardTotal.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent_blue))
        binding.cardTotal.statAccentBar.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_blue))

        // Good Card
        binding.cardGood.statTitle.text = "GOOD"
        binding.cardGood.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent_green))
        binding.cardGood.statAccentBar.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_green))

        // Defects Card
        binding.cardDefects.statTitle.text = "DEFECTS"
        binding.cardDefects.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.accent_red))
        binding.cardDefects.statAccentBar.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_red))
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // Update Stat Values
                    binding.cardTotal.statValue.text = "%,d".format(state.totalProcessed)
                    binding.cardGood.statValue.text = "%,d".format(state.goodCount)
                    binding.cardDefects.statValue.text = "%,d".format(state.defectiveCount)
                    
                    // Update Charts
                    binding.lineChart.setData(state.defectsPerMinute)
                    binding.pieChart.setData(state.goodCount, state.defectiveCount)
                    
                    // Update Feed
                    adapter.submitList(state.recentEvents)
                    binding.eventCount.text = "${state.recentEvents.size} events"
                }
            }
        }
    }
}
