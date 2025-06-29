package com.example.data.repository

import com.example.data.apiservice.ApiService
import com.example.domain.repository.TmdbRepository
import com.example.model.response.SearchResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.catch

class TmdbRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TmdbRepository {
    override suspend fun searchMulti(query: String, page: Int): Flow<Result<SearchResponseModel>> {
        return withContext(Dispatchers.IO) {
            flow {
                emit(Result.Loading)
                val response = apiService.searchMulti(query, page)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Result.Success(data = it))
                    } ?: emit(Result.Error(message = "Unknown error occurred", code = 0))
                } else {
                    emit(
                        Result.Error(
                            message = "Unknown error occurred",
                            code = response.code()
                        )
                    )
                }
            }.catch { error ->
                emit(Result.Error(message = error.message ?: "Unknown error occurred", code = 0))
            }
        }
    }
} 