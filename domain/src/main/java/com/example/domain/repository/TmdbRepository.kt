package com.example.domain.repository

import com.example.domain.utils.Result
import com.example.model.response.SearchResponseModel
import kotlinx.coroutines.flow.Flow

interface TmdbRepository {
    suspend fun searchMulti(query: String, page: Int = 1): Flow<Result<SearchResponseModel>>
} 