package com.example.githubuserfull.data.client.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubuserfull.data.client.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userEntity: UserEntity)

    @Delete
    suspend fun delete(userEntity: UserEntity)

    @Query("SELECT * from users where isFav = 1 ")
    fun getAllUsers(): LiveData<List<UserEntity>>

    @Query("SELECT EXISTS(SELECT * from users where login = :login AND isFav = 1)")
    suspend fun isUserExist(login: String): Boolean

    @Query("SELECT * from users where login = :login")
    fun getUser(login: String): LiveData<UserEntity>
}