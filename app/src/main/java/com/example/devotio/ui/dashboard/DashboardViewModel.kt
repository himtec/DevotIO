package com.example.devotio.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devotio.data.Temple
import com.example.devotio.data.TempleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = TempleRepository()

    private val _temples = MutableLiveData<List<Temple>>()
    val temples: LiveData<List<Temple>> = _temples

    private val _filteredTemples = MutableLiveData<List<Temple>>()
    val filteredTemples: LiveData<List<Temple>> = _filteredTemples

    private val _selectedTemple = MutableLiveData<Temple>()
    val selectedTemple: LiveData<Temple> = _selectedTemple

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _filterType = MutableLiveData<FilterType>()
    val filterType: LiveData<FilterType> = _filterType

    private var searchJob: Job? = null

    init {
        loadTemples()
        _filterType.value = FilterType.ALL
    }

    private fun loadTemples() {
        try {
            _temples.value = repository.getTemples()
            filterTemples()
        } catch (e: Exception) {
            _error.value = "Failed to load temples: ${e.message}"
        }
    }

    fun loadTempleDetails(templeId: Int) {
        try {
            _selectedTemple.value = repository.getTempleById(templeId)
        } catch (e: Exception) {
            _error.value = "Failed to load temple details: ${e.message}"
        }
    }

    fun toggleFavorite(templeId: Int) {
        try {
            val temple = _selectedTemple.value
            if (temple != null) {
                repository.toggleFavorite(templeId)
                _selectedTemple.value = temple.copy(isFavorite = !temple.isFavorite)
                filterTemples() // Refresh filtered list
            }
        } catch (e: Exception) {
            _error.value = "Failed to update favorite status: ${e.message}"
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce search
            filterTemples()
        }
    }

    fun setFilterType(type: FilterType) {
        _filterType.value = type
        filterTemples()
    }

    private fun filterTemples() {
        val query = _searchQuery.value?.lowercase() ?: ""
        val type = _filterType.value ?: FilterType.ALL
        val allTemples = _temples.value ?: emptyList()

        val filtered = allTemples.filter { temple ->
            val matchesSearch = query.isEmpty() ||
                    temple.name.lowercase().contains(query) ||
                    temple.address.lowercase().contains(query)

            val matchesFilter = when (type) {
                FilterType.ALL -> true
                FilterType.NEARBY -> true // TODO: Implement actual nearby filtering
                FilterType.FAVORITES -> temple.isFavorite
            }

            matchesSearch && matchesFilter
        }

        _filteredTemples.value = filtered
    }

    fun getFavoriteTemples(): List<Temple> {
        return repository.getFavoriteTemples()
    }

    enum class FilterType {
        ALL, NEARBY, FAVORITES
    }
}