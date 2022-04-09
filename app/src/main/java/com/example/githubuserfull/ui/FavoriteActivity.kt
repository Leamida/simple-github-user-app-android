package com.example.githubuserfull.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserfull.R
import com.example.githubuserfull.data.client.entity.UserEntity
import com.example.githubuserfull.databinding.ActivityFavoriteBinding
import com.example.githubuserfull.ui.adapter.ListUserDbAdapter
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModel
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModelFactory

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding : ActivityFavoriteBinding
    private lateinit var searchResultViewModel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListUser.layoutManager = LinearLayoutManager(this)
        val factory: SearchResultViewModelFactory = SearchResultViewModelFactory.getInstance(this@FavoriteActivity)
        val searchResultViewModels :SearchResultViewModel by viewModels {
            factory
        }
        searchResultViewModel = searchResultViewModels

        searchResultViewModel.getAllUsersfromDb().observe(this@FavoriteActivity) {
            it?.let {
                showRecyclerList(it)
                binding.pbList.visibility = View.GONE
            }
        }
        when(THEME){
            "dark"->binding.rvListUser.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
            "light"->binding.rvListUser.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun showRecyclerList(user: List<UserEntity?>) {
        val listUserDbAdapter = ListUserDbAdapter(user)
        binding.rvListUser.adapter = listUserDbAdapter
        listUserDbAdapter.setOnItemClickCallback(object : ListUserDbAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UserEntity) {
                parcelData(data)
            }
        })
    }
    private fun parcelData(user: UserEntity) {
        startActivity(
            Intent(this@FavoriteActivity, DetailUserActivity::class.java)
                .putExtra(DetailUserActivity.EXTRA_USER, user)
                .putExtra(DetailUserActivity.PARCEL_FROM, "FavoriteActivity")
        )
    }

    companion object{
        var THEME = "theme"
    }
}