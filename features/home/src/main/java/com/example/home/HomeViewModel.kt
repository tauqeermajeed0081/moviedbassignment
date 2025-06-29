package com.example.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.SearchMultiUseCase
import com.example.domain.utils.Result
import com.example.model.response.SearchItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val searchMultiUseCase: SearchMultiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Initial)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private var currentQuery = ""
    private var currentPage = 1
    private var hasMorePages = true

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.Search -> {
                performSearch(event.query, resetPagination = true)
            }

            is HomeUiEvent.LoadMore -> {
                if (hasMorePages && _uiState.value !is HomeUiState.Loading) {
                    performSearch(currentQuery, resetPagination = false)
                }
            }

            is HomeUiEvent.ItemClick -> {
            }
        }
    }

    private fun performSearch(query: String, resetPagination: Boolean) {
        if (resetPagination) {
            currentQuery = query
            currentPage = 1
            hasMorePages = true
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = HomeUiState.Initial
                return@launch
            }

            // Show loading state
            if (resetPagination) {
                _uiState.value = HomeUiState.Loading
            } else {
                // For pagination, keep current state and show loading more indicator
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(isLoadingMore = true)
                }
            }

            searchMultiUseCase(query, currentPage).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        if (resetPagination) {
                            _uiState.value = HomeUiState.Loading
                        }
                    }

                    is Result.Success -> {
                        val response = result.data
                        val newResults = response.results

                        if (resetPagination) {
                            // First page or new search
                            _uiState.value = HomeUiState.Success(
                                results = newResults,
                                currentPage = currentPage,
                                totalPages = response.totalPages,
                                isLoadingMore = false
                            )
                        } else {
                            // Pagination - append to existing results
                            val currentState = _uiState.value
                            if (currentState is HomeUiState.Success) {
                                val updatedResults = currentState.results + newResults
                                _uiState.value = currentState.copy(
                                    results = updatedResults,
                                    currentPage = currentPage,
                                    isLoadingMore = false
                                )
                            }
                        }

                        // Update pagination state
                        hasMorePages = currentPage < response.totalPages
                        currentPage++
                    }

                    is Result.Error -> {
                        if (resetPagination) {
                            _uiState.value = HomeUiState.Error(result.message)
                        } else {
                            // For pagination errors, keep current state but show error
                            val currentState = _uiState.value
                            if (currentState is HomeUiState.Success) {
                                _uiState.value = currentState.copy(
                                    isLoadingMore = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // For unit test only
    fun onSearch(text: String) = performSearch(text, resetPagination = true)
}

sealed class HomeUiState {
    object Initial : HomeUiState()
    object Loading : HomeUiState()
    data class Success(
        val results: List<SearchItem>,
        val currentPage: Int = 1,
        val totalPages: Int = 1,
        val isLoadingMore: Boolean = false,
        val errorMessage: String? = null
    ) : HomeUiState()

    data class Error(val message: String) : HomeUiState()
}

sealed class HomeUiEvent {
    data class Search(val query: String) : HomeUiEvent()
    data class ItemClick(val item: SearchItem) : HomeUiEvent()
    object LoadMore : HomeUiEvent()
} 