package com.example.dailyshoppinglist.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dailyshoppinglist.ShoppingApplication
import com.example.dailyshoppinglist.data.ShoppingRepository
import com.example.dailyshoppinglist.data.db.FrequentItem
import com.example.dailyshoppinglist.data.db.ShoppingItem
import com.example.dailyshoppinglist.data.settings.SettingsRepository
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.domain.QuickEntryParser
import com.example.dailyshoppinglist.domain.Suggestions
import com.example.dailyshoppinglist.domain.normalizedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ListUiState(
    val loading: Boolean = true,
    val toBuy: List<ShoppingItem> = emptyList(),
    val inCart: List<ShoppingItem> = emptyList(),
    val groupByCategory: Boolean = false,
    val frequent: List<FrequentItem> = emptyList(),
) {
    val total: Int get() = toBuy.size + inCart.size
    val isEmpty: Boolean get() = total == 0
    val progress: Float get() = if (total == 0) 0f else inCart.size.toFloat() / total

    val namesOnList: Set<String> = (toBuy + inCart).mapTo(HashSet()) { it.name.normalizedName() }

    val toBuyByCategory: List<Pair<Category, List<ShoppingItem>>>
        get() = toBuy.groupBy { Category.fromKey(it.category) }.entries
            .sortedBy { it.key.ordinal }
            .map { it.key to it.value }
}

class ListViewModel(
    private val repository: ShoppingRepository,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val query = MutableStateFlow("")

    val uiState: StateFlow<ListUiState> = combine(
        repository.items,
        repository.frequentItems(limit = 60),
        settings.settings,
    ) { items, frequent, prefs ->
        val (inCart, toBuy) = items.partition { it.isChecked }
        ListUiState(
            loading = false,
            toBuy = toBuy,
            inCart = inCart.sortedWith(compareBy({ it.checkedAt ?: 0L }, { it.id })),
            groupByCategory = prefs.groupByCategory,
            frequent = frequent,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ListUiState())

    val suggestions: StateFlow<List<FrequentItem>> = combine(query, uiState) { q, state ->
        Suggestions.rank(q, state.frequent, state.namesOnList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onQueryChange(text: String) {
        query.value = text
    }

    /** Adds the typed entry. Returns false if there was nothing to add. */
    fun submit(text: String): Boolean {
        val entry = QuickEntryParser.parse(text) ?: return false
        viewModelScope.launch { repository.addItem(entry.name, entry.quantity) }
        query.value = ""
        return true
    }

    fun addSuggestion(item: FrequentItem) {
        viewModelScope.launch { repository.addItem(item.name, 1, Category.fromKey(item.category)) }
        query.value = ""
    }

    fun toggle(item: ShoppingItem) {
        viewModelScope.launch { repository.setChecked(item, !item.isChecked) }
    }

    fun update(item: ShoppingItem) {
        viewModelScope.launch { repository.update(item) }
    }

    fun delete(item: ShoppingItem) {
        viewModelScope.launch { repository.delete(item) }
    }

    fun restore(items: List<ShoppingItem>) {
        viewModelScope.launch { repository.restore(items) }
    }

    /** Removes items in the cart without saving them. Returns what was removed, for undo. */
    fun clearChecked(): List<ShoppingItem> {
        val removed = uiState.value.inCart
        viewModelScope.launch { repository.deleteChecked() }
        return removed
    }

    fun clearAll(): List<ShoppingItem> {
        val removed = uiState.value.toBuy + uiState.value.inCart
        viewModelScope.launch { repository.deleteAll() }
        return removed
    }

    fun finishShopping() {
        viewModelScope.launch { repository.finishShopping() }
    }

    fun setGroupByCategory(enabled: Boolean) {
        viewModelScope.launch { settings.setGroupByCategory(enabled) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication
                ListViewModel(app.container.repository, app.container.settingsRepository)
            }
        }
    }
}
