package com.example.daizcode.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daizcode.R
import com.example.daizcode.databinding.LayoutDashboardBinding
import com.example.daizcode.ui.adapters.DetectionAdapter
import com.example.daizcode.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

/**
 * Fragment that displays the main dashboard with stats, charts, and live feed.
 * Shares the DashboardViewModel with the parent Activity.
 */
class DashboardFragment : Fragment() {

    private var _binding: LayoutDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private val detectionAdapter = DetectionAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        setupRecyclerView()
        setupStatCards()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = detectionAdapter
        }
    }

    private fun setupStatCards() {
        // Total Card
        binding.cardTotal.statTitle.text = "TOTAL"
        binding.cardTotal.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent_blue))
        binding.cardTotal.statAccentBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_blue))

        // Good Card
        binding.cardGood.statTitle.text = "GOOD"
        binding.cardGood.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent_green))
        binding.cardGood.statAccentBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_green))

        // Defects Card
        binding.cardDefects.statTitle.text = "DEFECTS"
        binding.cardDefects.statAccentDot.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.accent_red))
        binding.cardDefects.statAccentBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_red))
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    // Update Stat Values
                    binding.cardTotal.statValue.text = "%,d".format(state.totalProcessed)
                    binding.cardGood.statValue.text = "%,d".format(state.goodCount)
                    binding.cardDefects.statValue.text = "%,d".format(state.defectiveCount)

                    // Update Charts
                    binding.lineChart.setData(state.defectsPerMinute)
                    binding.pieChart.setData(state.goodCount, state.defectiveCount)

                    // Update Feed
                    detectionAdapter.submitList(state.recentEvents)
                    binding.eventCount.text = "${state.recentEvents.size} events"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
