package com.example.devotio.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val _upcomingFestivals = MutableLiveData<List<Festival>>()
    val upcomingFestivals: LiveData<List<Festival>> = _upcomingFestivals

    init {
        loadUpcomingFestivals()
    }

    private fun loadUpcomingFestivals() {
        // TODO: Load festivals from a data source
        val festivals = listOf(
            Festival("Diwali", "November 12, 2024", "Festival of Lights"),
            Festival("Holi", "March 25, 2024", "Festival of Colors"),
            Festival("Raksha Bandhan", "August 19, 2024", "Celebration of Sibling Love")
        )
        _upcomingFestivals.value = festivals
    }
}

data class Festival(
    val name: String,
    val date: String,
    val description: String
) 