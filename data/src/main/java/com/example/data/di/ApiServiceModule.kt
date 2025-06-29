package com.example.data.di

import com.example.data.apiservice.ApiService
import com.example.data.repository.TmdbRepositoryImpl
import com.example.domain.repository.TmdbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideTmdbRepository(apiService: ApiService): TmdbRepository =
        TmdbRepositoryImpl(apiService)
} 