package com.example.devotio.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devotio.data.Temple
import com.example.devotio.data.TempleRepository

class TempleViewModel : ViewModel() {
    private val repository = TempleRepository()
    private val _temples = MutableLiveData<List<Temple>>()
    val temples: LiveData<List<Temple>> = _temples

    private val _selectedTemple = MutableLiveData<Temple>()
    val selectedTemple: LiveData<Temple> = _selectedTemple

    private var currentFilter = FilterType.ALL
    private var currentSearchQuery = ""

    init {
        loadTemples()
    }

    private fun loadTemples() {
        val allTemples = when (currentFilter) {
            FilterType.ALL -> repository.getTemples()
            FilterType.NEARBY -> repository.getTemples().filter { it.isNearby }
            FilterType.FAVORITES -> repository.getFavoriteTemples()
        }

        _temples.value = if (currentSearchQuery.isBlank()) {
            allTemples
        } else {
            allTemples.filter { temple ->
                temple.name.contains(currentSearchQuery, ignoreCase = true) ||
                temple.address.contains(currentSearchQuery, ignoreCase = true) ||
                temple.deity.contains(currentSearchQuery, ignoreCase = true)
            }
        }
    }

    fun selectTemple(templeId: Int) {
        _selectedTemple.value = repository.getTempleById(templeId)
    }

    fun toggleFavorite(templeId: Int) {
        val currentTemples = _temples.value ?: return
        val updatedTemples = currentTemples.map { temple ->
            if (temple.id == templeId) {
                temple.copy(isFavorite = !temple.isFavorite)
            } else {
                temple
            }
        }
        _temples.value = updatedTemples
        repository.toggleFavorite(templeId)
    }

    fun setFilter(filterType: FilterType) {
        currentFilter = filterType
        loadTemples()
    }

    fun setSearchQuery(query: String) {
        currentSearchQuery = query
        loadTemples()
    }

    fun getFavoriteTemples(): List<Temple> {
        return repository.getFavoriteTemples()
    }

    enum class FilterType {
        ALL, NEARBY, FAVORITES
    }
} 