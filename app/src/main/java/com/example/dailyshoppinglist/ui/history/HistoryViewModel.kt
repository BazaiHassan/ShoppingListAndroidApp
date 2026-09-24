package com.example.dailyshoppinglist.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dailyshoppinglist.ShoppingApplication
import com.example.dailyshoppinglist.data.ShoppingRepository
import com.example.dailyshoppinglist.data.db.FrequentItem
import com.example.dailyshoppinglist.data.db.TripItem
import com.example.dailyshoppinglist.data.db.TripWithItems
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.domain.normalizedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryUiState(
    val loading: Boolean = true,
    val allTrips: List<TripWithItems> = emptyList(),
    val trips: List<TripWithItems> = emptyList(),
    val frequent: List<FrequentItem> = emptyList(),
) {
    val hasHistory: Boolean get() = allTrips.isNotEmpty()
    fun trip(id: Long?): TripWithItems? = id?.let { allTrips.firstOrNull { trip -> trip.trip.id == it } }
}

class HistoryViewModel(private val repository: ShoppingRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<HistoryUiState> = combine(
        repository.trips,
        repository.frequentItems(limit = 15),
        _query,
    ) { trips, frequent, query ->
        val q = query.normalizedName()
        HistoryUiState(
            loading = false,
            allTrips = trips,
            trips = if (q.isEmpty()) trips else trips.filter { trip -> trip.items.any { it.name.normalizedName().contains(q) } },
            frequent = frequent,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun addToList(item: TripItem) {
        viewModelScope.launch { repository.addItem(item.name, item.quantity, Category.fromKey(item.category)) }
    }

    fun addToList(item: FrequentItem) {
        viewModelScope.launch { repository.addItem(item.name, 1, Category.fromKey(item.category)) }
    }

    fun addAllToList(items: List<TripItem>) {
        viewModelScope.launch { repository.addAll(items) }
    }

    fun deleteTrip(id: Long) {
        viewModelScope.launch { repository.deleteTrip(id) }
    }

    fun clearHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication
                HistoryViewModel(app.container.repository)
            }
        }
    }
}
