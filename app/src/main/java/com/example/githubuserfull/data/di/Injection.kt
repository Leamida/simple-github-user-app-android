package com.example.githubuserfull.data.di

import android.content.Context
import com.example.githubuserfull.data.client.room.UserRoomDatabase
import com.example.githubuserfull.data.repository.SearchResultRepository
import com.example.githubuserfull.data.server.retrofit.ApiConfig

object Injection {
    fun provideRepository(context : Context):SearchResultRepository {
        val apiService = ApiConfig.getApiService()
        val database = UserRoomDatabase.getDatabase(context)
        val dao = database.userDao()
        return SearchResultRepository.getInstance(apiService,dao)
    }
}