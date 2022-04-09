package com.example.githubuserfull.data.client.entity

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "users")
@Parcelize
data class UserEntity(
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "login")
    var login: String,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "avatar")
    var avatar: String? = null,

    @ColumnInfo(name = "follower")
    var follower: Int? = null,

    @ColumnInfo(name = "following")
    var following: Int? = null,

    @ColumnInfo(name = "repo")
    var repo: Int? = null,

    @ColumnInfo(name = "isFav")
    var isFav:Boolean? = null

) : Parcelable