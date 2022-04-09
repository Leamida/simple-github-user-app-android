package com.example.githubuserfull.data.repository

import androidx.lifecycle.*
import com.example.githubuserfull.data.client.entity.UserEntity
import com.example.githubuserfull.data.client.room.UserDao
import com.example.githubuserfull.data.server.response.ItemsItem
import com.example.githubuserfull.data.server.retrofit.ApiService

class SearchResultRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    private val _users = MutableLiveData<List<ItemsItem?>?>()
    val users: LiveData<List<ItemsItem?>?> = _users

    private val _user = MutableLiveData<UserEntity>()
    private val user: LiveData<UserEntity> = _user

    private val _follower = MutableLiveData<List<ItemsItem?>>()
    private val follower: LiveData<List<ItemsItem?>> = _follower

    private val _following = MutableLiveData<List<ItemsItem?>>()
    private val following: LiveData<List<ItemsItem?>> = _following


    fun removeValue() {
        _users.value = listOf(ItemsItem())
    }

    fun searchUser(login: String): LiveData<UserResult<List<ItemsItem?>?>> = liveData {
        emit(UserResult.Loading)
        try {
            val user = apiService.getUser(login)
            if (user.totalCount!! > 0) {
                user.items?.let {
                    _users.value = it
                    val tempData: LiveData<UserResult<List<ItemsItem?>?>> =
                        users.map { map -> UserResult.Success(map) }
                    emitSource(tempData)
                }
            } else {
                removeValue()
                val tempData: LiveData<UserResult<List<ItemsItem?>?>> =
                    users.map { map -> UserResult.Success(map) }
                emitSource(tempData)
                emit(UserResult.Error("user tidak ditemukan"))
            }
        } catch (e: Exception) {
            emit(UserResult.Error(e.message.toString()))
        }
    }

    fun getDetailUser(login: String): LiveData<UserResult<UserEntity>> = liveData {
        emit(UserResult.Loading)
        try {
            val response = apiService.getDetailUser(login)
            _user.value = response.login?.let {
                val isFav = userDao.isUserExist(login)
                UserEntity(
                    it,
                    response.name,
                    response.avatarUrl,
                    response.followers,
                    response.following,
                    response.publicRepos,
                    isFav
                )
            }
            val tempData: LiveData<UserResult<UserEntity>> =
                user.map { UserResult.Success(it) }
            emitSource(tempData)
        } catch (e: Exception) {
            emit(UserResult.Error(e.message.toString()))
        }
    }

    suspend fun addToFavorite(userEntity: UserEntity) {
        if (!userDao.isUserExist(userEntity.login)) {
            userDao.insert(userEntity)
        }
    }

    suspend fun removeFromFavorite(userEntity: UserEntity) {
        if (userDao.isUserExist(userEntity.login)) {
            userDao.delete(userEntity)
        }
    }

    fun getUsersFromDb(): LiveData<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    fun getDetailUserFromDb(login: String): LiveData<UserEntity> {
        return userDao.getUser(login)
    }

    fun getFollower(login: String): LiveData<UserResult<List<ItemsItem?>>> =
        liveData {
            emit(UserResult.Loading)
            try {
                _follower.value = apiService.getListFollower(login)
            } catch (e: Exception) {
                emit(UserResult.Error(e.message.toString()))
            }
            val tempData: LiveData<UserResult<List<ItemsItem?>>> =
                follower.map { UserResult.Success(it) }
            emitSource(tempData)
        }

    fun getFollowing(login: String): LiveData<UserResult<List<ItemsItem?>>> = liveData {
        emit(UserResult.Loading)
        try {
            _following.value = apiService.getListFollowing(login)
        } catch (e: Exception) {
            emit(UserResult.Error(e.message.toString()))
        }

        val tempData: LiveData<UserResult<List<ItemsItem?>>> =
            following.map { UserResult.Success(it) }
        emitSource(tempData)
    }


    companion object {
        @Volatile
        private var instance: SearchResultRepository? = null
        fun getInstance(
            apiService: ApiService,
            userDao: UserDao
        ): SearchResultRepository =
            instance ?: synchronized(this) {
                instance ?: SearchResultRepository(apiService, userDao)
            }.also { instance = it }
    }
}