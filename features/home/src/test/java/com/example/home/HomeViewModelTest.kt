package com.example.home

import com.example.domain.usecase.SearchMultiUseCase
import com.example.model.response.SearchItem
import com.example.model.response.SearchResponseModel
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // Mock the use-case
    private val searchUseCase: SearchMultiUseCase = mockk()

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)      // <-— coroutine-test rule replacement
        viewModel = HomeViewModel(searchUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `first page success emits Loading then Success`() = testScope.runTest {
        /* Given */
        val fakeResponse = SearchResponseModel(
            page = 1,
            results = listOf(
                SearchItem(
                    id = 0,
                    title = "Tokyo",
                    name = "Tokyo",
                    originalTitle = "",
                    originalName = "",
                    overview = "",
                    posterPath = "",
                    backdropPath = "",
                    mediaType = "",
                    originalLanguage = "",
                    genreIds = listOf(),
                    popularity = 1.5,
                    voteAverage = 1.5,
                    voteCount = 1,
                    adult = false,
                    releaseDate = "",
                    firstAirDate = "",
                    video = false,
                    originCountry = listOf(),
                )
            ),
            totalPages = 1,
            totalResults = 1
        )
        coEvery { searchUseCase("tokyo", 1) } returns flow {
            emit(com.example.domain.utils.Result.Loading)
            emit(com.example.domain.utils.Result.Success(fakeResponse))
        }

        /* When */
        viewModel.onSearch("tokyo")

        /* Then */
        viewModel.uiState.collect { it ->
            assertTrue(it is HomeUiState.Loading)
            val success = it as HomeUiState.Success
            assertEquals(fakeResponse.results, success.results)
        }
    }
}