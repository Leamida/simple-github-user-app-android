package com.example.githubuserfull.ui.viewmodel.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubuserfull.data.di.Injection
import com.example.githubuserfull.data.repository.SearchResultRepository


class SearchResultViewModelFactory private constructor(private val searchResultRepository: SearchResultRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchResultViewModel::class.java)) {
            return SearchResultViewModel(searchResultRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: SearchResultViewModelFactory? = null
        fun getInstance(context : Context): SearchResultViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SearchResultViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}