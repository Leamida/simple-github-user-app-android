package com.example.githubuserfull

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserfull.data.repository.UserResult
import com.example.githubuserfull.data.server.response.ItemsItem
import com.example.githubuserfull.databinding.ActivityMainBinding
import com.example.githubuserfull.ui.DetailUserActivity
import com.example.githubuserfull.ui.FavoriteActivity
import com.example.githubuserfull.ui.FollowFragment
import com.example.githubuserfull.ui.adapter.ListUserAdapter
import com.example.githubuserfull.ui.viewmodel.theme.ThemeViewModel
import com.example.githubuserfull.ui.viewmodel.theme.ThemeViewModelFactory
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModel
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModelFactory
import com.example.githubuserfull.utils.SettingPreferences
import com.example.githubuserfull.utils.isInternet

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_setting")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var themeViewModel: ThemeViewModel
    private var isDark: Boolean? = null
    private lateinit var searchResultViewModel: SearchResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListUser.layoutManager = LinearLayoutManager(this)
        val pref = SettingPreferences.getInstance(dataStore)
        themeViewModel =
            ViewModelProvider(this, ThemeViewModelFactory(pref))[ThemeViewModel::class.java]
        val factory: SearchResultViewModelFactory  = SearchResultViewModelFactory.getInstance(this@MainActivity)
        val searchResultViewModels :SearchResultViewModel by viewModels {
            factory
        }
        searchResultViewModel = searchResultViewModels
        searchUser()
        searchResultViewModel.getUsers().observe(this@MainActivity) {
            it?.let {
               if(it[0]?.login != null){
                   showRecyclerList(it)
                   binding.tvDataNull.visibility = View.GONE
               }else{
                   binding.tvDataNull.visibility = View.VISIBLE
               }
            }
        }
        binding.tvDataNull.visibility = View.VISIBLE
    }

    private fun showRecyclerList(user: List<ItemsItem?>) {
        val listUserAdapter = ListUserAdapter(user)
        binding.rvListUser.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {
                parcelData(data)
            }
        })
    }

    private fun parcelData(user: ItemsItem) {
        startActivity(
            Intent(this@MainActivity, DetailUserActivity::class.java)
                .putExtra(DetailUserActivity.EXTRA_USER, user)
                .putExtra(DetailUserActivity.PARCEL_FROM, "MainActivity")
        )
    }

    private fun searchUser() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    if (isInternet.Available(this@MainActivity)) {
                        searchUser(query)
                        searchView.clearFocus()
                    } else {
                        Toast.makeText(this@MainActivity, "tidak ada internet!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    return true
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
    }

    private fun searchUser(query:String){
        searchResultViewModel.searchUser(query).observe(this@MainActivity) {
            it?.let {
                when (it) {
                    is UserResult.Loading -> {
                        binding.apply {
                            pbList.visibility = View.VISIBLE
                            tvDataNull.visibility = View.GONE
                        }
                    }
                    is UserResult.Success -> {
                        binding.apply {
                            rvListUser.visibility = View.VISIBLE
                            pbList.visibility = View.GONE
                            tvDataNull.visibility = View.GONE
                        }
                        it.data?.let { items -> showRecyclerList(items) }
                    }
                    is UserResult.Error -> {
                        binding.apply {
                            rvListUser.visibility = View.GONE
                            pbList.visibility = View.GONE
                            tvDataNull.visibility = View.VISIBLE
                            tvDataNull.text = "Terjadi kesalahan " + it.error
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        val theme = menu.findItem(R.id.themeSwitcher)
        themeViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                theme.icon = ContextCompat.getDrawable(this, R.drawable.ic_light)
                binding.searchView.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.midnightblue
                    )
                )
                FollowFragment.THEME = "dark"
                FavoriteActivity.THEME = "dark"
                binding.rvListUser.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
                isDark = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                theme.icon = ContextCompat.getDrawable(this, R.drawable.ic_moon)
                FollowFragment.THEME = "light"
                FavoriteActivity.THEME = "light"
                isDark = false
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.themeSwitcher -> {
                if (isDark!!) {
                    themeViewModel.saveThemeSetting(false)
                } else {
                    themeViewModel.saveThemeSetting(true)
                }
                true
            }
            R.id.userFavorite -> {
                startActivity(
                    Intent(this@MainActivity, FavoriteActivity::class.java)
                )
                true
            }
            else -> true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        searchResultViewModel.removeValue()
    }
}