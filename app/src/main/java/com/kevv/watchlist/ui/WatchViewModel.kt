package com.kevv.watchlist.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kevv.watchlist.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ListState(val items: List<Title> = emptyList(), val total: Int = 0)

class WatchViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = WatchRepository(AppDatabase.get(app).dao())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query
    private val _filter = MutableStateFlow<Status?>(null)
    val filter: StateFlow<Status?> = _filter

    val list: StateFlow<ListState> =
        combine(repo.titles, _query, _filter) { all, q, f ->
            ListState(filterTitles(all, q, f), all.size)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

    fun setQuery(q: String) { _query.value = q }
    fun setFilter(s: Status?) { _filter.value = s }
    fun save(t: Title) = viewModelScope.launch { repo.save(t) }
    fun delete(t: Title) = viewModelScope.launch { repo.delete(t) }
}
