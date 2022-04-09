package com.example.githubuserfull.data.server.retrofit

import com.example.githubuserfull.BuildConfig
import com.example.githubuserfull.data.server.response.DetailUserResponse
import com.example.githubuserfull.data.server.response.ItemsItem
import com.example.githubuserfull.data.server.response.UserResponse
import retrofit2.http.*

interface ApiService {

    @GET("search/users")
    suspend fun getUser(
        @Query("q") username: String
    ): UserResponse

    @GET("users/{username}")
    suspend fun getDetailUser(
        @Path("username") username: String
    ): DetailUserResponse

    @GET("users/{username}/followers")
    suspend fun getListFollower(
        @Path("username") id: String
    ): List<ItemsItem>

    @GET("users/{username}/following")
    suspend fun getListFollowing(
        @Path("username") id: String
    ): List<ItemsItem>
}