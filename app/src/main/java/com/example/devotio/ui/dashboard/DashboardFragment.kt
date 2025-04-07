package com.example.devotio.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devotio.R
import com.example.devotio.databinding.FragmentDashboardBinding
import com.example.devotio.viewmodel.TempleViewModel

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TempleViewModel by viewModels()
    private lateinit var templeAdapter: TempleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
        setupFilters()
        observeTemples()
    }

    private fun setupSearchView() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Handle search text changes
                viewModel.setSearchQuery(s?.toString() ?: "")
            }
        })
    }

    private fun setupFilters() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.all_chip -> viewModel.setFilter(TempleViewModel.FilterType.ALL)
                R.id.nearby_chip -> viewModel.setFilter(TempleViewModel.FilterType.NEARBY)
                R.id.favorites_chip -> viewModel.setFilter(TempleViewModel.FilterType.FAVORITES)
            }
        }
    }

    private fun setupRecyclerView() {
        templeAdapter = TempleAdapter { temple ->
            findNavController().navigate(
                R.id.action_dashboard_to_temple_detail,
                Bundle().apply {
                    putInt("templeId", temple.id)
                }
            )
        }
        binding.templesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = templeAdapter
        }
    }

    private fun observeTemples() {
        viewModel.temples.observe(viewLifecycleOwner) { temples ->
            templeAdapter.submitList(temples)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}