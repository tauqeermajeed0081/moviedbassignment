package com.example.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.core.utils.AppConstants
import com.example.model.response.SearchItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun HomeRoute(
    onHomeItemClick: (SearchItem) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen(
        onHomeItemClick = onHomeItemClick,
        viewModel = viewModel
    )
}

@Composable
fun HomeScreen(
    onHomeItemClick: (SearchItem) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(searchQuery) {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(500) // 500ms debounce
            viewModel.onEvent(HomeUiEvent.Search(searchQuery))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 35.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            is HomeUiState.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start typing to search",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Success -> {
                val successState = uiState as HomeUiState.Success
                val results = successState.results
                val groupedByMediaType = results.groupBy { it.mediaType }
                val sortedMediaTypes = groupedByMediaType.keys.sorted()

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sortedMediaTypes) { mediaType ->
                            val mediaTypeItems = groupedByMediaType[mediaType] ?: emptyList()
                            MediaTypeCarousel(
                                mediaType = mediaType,
                                items = mediaTypeItems,
                                onItemClick = { item ->
                                    viewModel.onEvent(HomeUiEvent.ItemClick(item))
                                    onHomeItemClick(item)
                                }
                            )
                        }

                        // Load more section
                        item {
                            LoadMoreSection(
                                currentPage = successState.currentPage,
                                totalPages = successState.totalPages,
                                isLoadingMore = successState.isLoadingMore,
                                errorMessage = successState.errorMessage,
                                onLoadMore = {
                                    viewModel.onEvent(HomeUiEvent.LoadMore)
                                }
                            )
                        }
                    }
                }
            }

            is HomeUiState.Error -> {
                val errorState = uiState as HomeUiState.Error
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorState.message,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaTypeCarousel(
    mediaType: String,
    items: List<SearchItem>,
    onItemClick: (SearchItem) -> Unit
) {
    Column {
        Text(
            text = mediaType.uppercase(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                SearchItemCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun SearchItemCard(
    item: SearchItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Poster Image
            AsyncImage(
                model = "${AppConstants.ImagePath}${item.posterPath}",
                contentDescription = item.title ?: item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            // Title
            Text(
                text = item.title ?: item.name ?: "Unknown",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun LoadMoreSection(
    currentPage: Int,
    totalPages: Int,
    isLoadingMore: Boolean,
    errorMessage: String?,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonEnabled = currentPage < totalPages && !isLoadingMore && errorMessage == null

        if (currentPage < totalPages) {
            Button(
                onClick = onLoadMore,
                enabled = buttonEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoadingMore) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = if (isLoadingMore) "Loading" else "Load More",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Text(
                text = "All results loaded",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Error: $errorMessage",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
        }
    }
}

