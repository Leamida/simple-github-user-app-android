package com.example.githubuserfull.ui.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubuserfull.data.client.entity.UserEntity
import com.example.githubuserfull.data.repository.SearchResultRepository
import kotlinx.coroutines.launch

class SearchResultViewModel(
    private val searchResultRepository: SearchResultRepository
) : ViewModel() {
    fun searchUser(login: String) = searchResultRepository.searchUser(login)
    fun getUsers() = searchResultRepository.users
    fun removeValue() = searchResultRepository.removeValue()
    fun getDetailUser(login: String) = searchResultRepository.getDetailUser(login)
    fun getFollower(login: String) = searchResultRepository.getFollower(login)
    fun getFollowing(login: String) = searchResultRepository.getFollowing(login)
    fun getAllUsersfromDb() = searchResultRepository.getUsersFromDb()
    fun getDetailUserFromDb(login: String) = searchResultRepository.getDetailUserFromDb(login)
    fun addToFavorite(userEntity: UserEntity) {
        viewModelScope.launch {
            searchResultRepository.addToFavorite(userEntity)
        }
    }
    fun removeFromFavorite(userEntity: UserEntity) {
        viewModelScope.launch {
            searchResultRepository.removeFromFavorite(userEntity)
        }
    }

}