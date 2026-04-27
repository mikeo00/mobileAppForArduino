package com.example.daizcode.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.daizcode.R
import com.example.daizcode.data.model.DetectionEvent
import com.example.daizcode.data.model.DetectionStatus
import com.example.daizcode.data.model.ObjectType
import com.example.daizcode.databinding.FragmentPartsBinding
import com.example.daizcode.ui.adapters.PartsAdapter
import com.example.daizcode.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch

/**
 * Fragment that displays all scanned parts, filterable by type and status.
 * Each item shows the part type, good/bad status, confidence, and scan time.
 */
class PartsFragment : Fragment() {

    private var _binding: FragmentPartsBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DashboardViewModel
    private val partsAdapter = PartsAdapter()

    // Current filter state
    private var selectedType: ObjectType? = null   // null = All
    private var selectedStatus: DetectionStatus? = null // null = All

    // All events from ViewModel
    private var allEvents: List<DetectionEvent> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPartsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DashboardViewModel::class.java]

        setupRecyclerView()
        setupTypeChips()
        setupStatusChips()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.partsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = partsAdapter
        }
    }

    private fun setupTypeChips() {
        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            selectedType = when (checkedIds.first()) {
                R.id.chipBolt -> ObjectType.BOLT
                R.id.chipNut -> ObjectType.NUT
                R.id.chipGear -> ObjectType.GEAR
                R.id.chipScrew -> ObjectType.SCREW
                else -> null // chipAll
            }
            applyFilters()
        }
    }

    private fun setupStatusChips() {
        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            selectedStatus = when (checkedIds.first()) {
                R.id.chipGood -> DetectionStatus.GOOD
                R.id.chipBad -> DetectionStatus.DEFECTIVE
                else -> null // chipStatusAll
            }
            applyFilters()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    allEvents = state.recentEvents
                    applyFilters()
                }
            }
        }
    }

    private fun applyFilters() {
        var filtered = allEvents

        // Filter by type
        if (selectedType != null) {
            filtered = filtered.filter { it.objectType == selectedType }
        }

        // Filter by status
        if (selectedStatus != null) {
            filtered = filtered.filter { it.status == selectedStatus }
        }

        // Sort by timestamp (newest first)
        filtered = filtered.sortedByDescending { it.timestamp }

        // Update UI
        partsAdapter.submitList(filtered)
        binding.resultsCount.text = "${filtered.size} result${if (filtered.size != 1) "s" else ""}"

        // Show/hide empty state
        if (filtered.isEmpty()) {
            binding.partsRecyclerView.visibility = View.GONE
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.partsRecyclerView.visibility = View.VISIBLE
            binding.emptyState.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
