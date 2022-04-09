package com.example.githubuserfull.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.githubuserfull.R
import com.example.githubuserfull.data.client.entity.UserEntity
import com.example.githubuserfull.data.repository.UserResult
import com.example.githubuserfull.data.server.response.ItemsItem
import com.example.githubuserfull.databinding.ActivityDetailUserBinding
import com.example.githubuserfull.ui.adapter.SectionsPagerAdapter
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModel
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModelFactory
import com.example.githubuserfull.utils.isInternet
import com.google.android.material.tabs.TabLayoutMediator

class DetailUserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailUserBinding
    private val factory: SearchResultViewModelFactory =
        SearchResultViewModelFactory.getInstance(this@DetailUserActivity)
    private val searchResultViewModel: SearchResultViewModel by viewModels {
        factory
    }
    private lateinit var userEntity: UserEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        attachFragment()
        getParcelData()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUI(userEntity: UserEntity) {
        binding.apply {
            userEntity.apply {
                tvName.text = name
                tvUsername.text = login
                tvFollower.text = splitInt(follower, "Follower")
                tvFollowing.text = splitInt(following, "Following")
                tvRepo.text = splitInt(repo, "Repo")
                Glide.with(this@DetailUserActivity)
                    .load(avatar)
                    .transform(CircleCrop())
                    .into(ivAvatar)
            }

            fabFavorite.setOnClickListener {
                Log.d("UserEntity",userEntity.avatar.toString())
                if (this@DetailUserActivity.userEntity.avatar!=null) {
                    addRemoveUser(userEntity)
                } else {
                    binding.fabFavorite.visibility = View.GONE
                }
            }
        }
    }

    private fun setUserEntity(userEntity: UserEntity, isFav:Boolean){
        this@DetailUserActivity.userEntity = UserEntity(
            userEntity.login,
            userEntity.name,
            userEntity.avatar,
            userEntity.follower,
            userEntity.following,
            userEntity.repo,
            isFav
        )
    }

    private fun addRemoveUser(userEntity: UserEntity) {
        when (this@DetailUserActivity.userEntity.isFav) {
            true -> {
                setUserEntity(userEntity,false)
                searchResultViewModel.removeFromFavorite(this@DetailUserActivity.userEntity)
                binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_unfilled))
            }
            false -> {
                setUserEntity(userEntity,true)
                searchResultViewModel.addToFavorite(this@DetailUserActivity.userEntity)
                binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite))
            }
            else -> {

            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getParcelData() {
        when (intent.getStringExtra(PARCEL_FROM)) {
            "MainActivity" -> {
                val user = intent.getParcelableExtra<ItemsItem>(EXTRA_USER)
                binding.apply {
                    user?.login?.let {
                        getDetailUser(it)
                        FollowFragment.ARG_USER = it
                    }
                }
            }
            "FavoriteActivity" -> {
                val user = intent.getParcelableExtra<UserEntity>(EXTRA_USER)
                binding.apply {
                    user?.let {
                        it.isFav?.let { isFav -> setUserEntity(it, isFav) }
                        setUI(user)
                        FollowFragment.ARG_USER = user.login
                        if (user.isFav == true) {
                            binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite))
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getDetailUser(login: String) {
        if (isInternet.Available(this@DetailUserActivity)) {
            searchResultViewModel.getDetailUser(login).observe(this@DetailUserActivity) { user ->
                user?.let { result ->
                    when (result) {
                        is UserResult.Loading -> {

                        }
                        is UserResult.Success -> {
                            result.data.isFav?.let { setUserEntity(result.data, it) }
                            setUI(userEntity)
                            if (result.data.isFav == true) {
                                binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite))
                            } else {
                                binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_unfilled))
                            }
                        }
                        is UserResult.Error -> {
                            Toast.makeText(
                                this@DetailUserActivity, "Terjadi Kesalahan" +
                                        result.error, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            searchResultViewModel.getDetailUserFromDb(login).observe(this@DetailUserActivity) {
                it?.let { user->
                    user.isFav?.let { it -> setUserEntity(user, it) }
                    if (user.isFav == true) {
                        binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite))
                    } else {
                        binding.fabFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_unfilled))
                    }
                    setUI(userEntity)
                }
            }
        }
    }

    private fun splitInt(num: Int?, str: String): String {
        return if (num!! > 1_000) {
            "•$str " + ("%.1f".format(num.toDouble() / 1_000)) + "K"
        } else if (num > 1_000_000) {
            "•$str " + ("%.1f".format(num.toDouble() / 1_000_000)) + "M"
        } else {
            "•$str $num"
        }
    }

    private fun attachFragment() {
        val sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.apply {
            vpFollow.adapter = sectionsPagerAdapter
            TabLayoutMediator(tabs, vpFollow) { tab, position ->
                tab.text = TAB_TITLES[position]
            }.attach()
        }
    }

    companion object {
        const val EXTRA_USER = "extra_user"
        private val TAB_TITLES = arrayOf(
            "Follower",
            "Following"
        )
        const val PARCEL_FROM = "_from"
    }
}