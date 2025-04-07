package com.example.devotio.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devotio.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarViewModel: CalendarViewModel
    private lateinit var festivalAdapter: FestivalAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        calendarViewModel = ViewModelProvider(this)[CalendarViewModel::class.java]
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        festivalAdapter = FestivalAdapter()
        binding.festivalsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = festivalAdapter
        }
    }

    private fun setupObservers() {
        calendarViewModel.upcomingFestivals.observe(viewLifecycleOwner) { festivals ->
            festivalAdapter.submitList(festivals)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 