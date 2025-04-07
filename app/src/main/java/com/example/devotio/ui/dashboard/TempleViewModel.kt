package com.example.devotio.ui.dashboard

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devotio.data.Temple
import com.example.devotio.data.TempleEvent
import com.example.devotio.data.TempleRepository
import kotlinx.coroutines.launch
import java.util.*

class TempleViewModel : ViewModel() {

    private val repository = TempleRepository()
    private val _temples = MutableLiveData<List<Temple>>()
    val temples: LiveData<List<Temple>> = _temples

    private val _selectedTemple = MutableLiveData<Temple>()
    val selectedTemple: LiveData<Temple> = _selectedTemple

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _searchQuery = MutableLiveData<String>()
    val searchQuery: LiveData<String> = _searchQuery

    private val _filteredTemples = MutableLiveData<List<Temple>>()
    val filteredTemples: LiveData<List<Temple>> = _filteredTemples

    private val _templeDetails = MutableLiveData<Temple>()
    val templeDetails: LiveData<Temple> = _templeDetails

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        loadTemples()
        setupSearchAndFilter()
    }

    private fun loadTemples() {
        viewModelScope.launch {
            try {
                val temples = repository.getTemples()
                _temples.value = temples
                _filteredTemples.value = temples
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    private fun setupSearchAndFilter() {
        viewModelScope.launch {
            _searchQuery.observeForever { query ->
                filterTemples(query)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterTemples(query: String?) {
        val temples = _temples.value ?: return
        if (query.isNullOrBlank()) {
            _filteredTemples.value = temples
            return
        }

        val filtered = temples.filter { temple ->
            temple.name.contains(query, ignoreCase = true) ||
            temple.deity.contains(query, ignoreCase = true) ||
            temple.address.contains(query, ignoreCase = true)
        }
        _filteredTemples.value = filtered
    }

    fun selectTemple(templeId: Int) {
        viewModelScope.launch {
            try {
                val temple = repository.getTempleById(templeId)
                temple?.let {
                    _selectedTemple.value = it
                    _isFavorite.value = it.isFavorite
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            try {
                _templeDetails.value?.let { temple ->
                    repository.toggleFavorite(temple.id)
                    _templeDetails.value = temple.copy(isFavorite = !temple.isFavorite)
                    _isFavorite.value = !temple.isFavorite
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun shareTemple() {
        _templeDetails.value?.let { temple ->
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, temple.name)
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Check out ${temple.name} at ${temple.address}"
                )
            }
            _error.value = "Share intent created"
        }
    }

    fun openDirections() {
        _templeDetails.value?.let { temple ->
            val uri = "geo:${temple.latitude},${temple.longitude}?q=${Uri.encode(temple.name)}"
            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            mapIntent.setPackage("com.google.android.apps.maps")
            _error.value = "Directions intent created"
        }
    }

    fun loadTempleDetails(templeId: Int) {
        viewModelScope.launch {
            try {
                val temple = repository.getTempleById(templeId)
                temple?.let {
                    _templeDetails.value = it
                    _isFavorite.value = it.isFavorite
                }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }
} 