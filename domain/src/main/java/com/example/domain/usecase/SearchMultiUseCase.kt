package com.example.domain.usecase

import com.example.domain.repository.TmdbRepository
import com.example.domain.utils.Result
import com.example.model.response.SearchResponseModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMultiUseCase @Inject constructor(
    private val repository: TmdbRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Flow<Result<SearchResponseModel>> {
        return repository.searchMulti(query, page)
    }
} 