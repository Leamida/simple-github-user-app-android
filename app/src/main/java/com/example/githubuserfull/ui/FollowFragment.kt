package com.example.githubuserfull.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubuserfull.R
import com.example.githubuserfull.data.repository.UserResult
import com.example.githubuserfull.data.server.response.ItemsItem
import com.example.githubuserfull.databinding.FragmentFollowBinding
import com.example.githubuserfull.ui.adapter.ListUserAdapter
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModel
import com.example.githubuserfull.ui.viewmodel.user.SearchResultViewModelFactory
import com.example.githubuserfull.utils.isInternet


class FollowFragment : Fragment() {
    private lateinit var binding: FragmentFollowBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFollowBinding.inflate(layoutInflater)
        binding.rvListUser.layoutManager = LinearLayoutManager(activity)
        when(THEME){
            "dark"->binding.rvListUser.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.black))
            "light"->binding.rvListUser.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.white))

        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory: SearchResultViewModelFactory =
            SearchResultViewModelFactory.getInstance(requireActivity())
        val searchResultViewModel: SearchResultViewModel by viewModels {
            factory
        }
        if (isInternet.Available(requireActivity())){
            when (arguments?.getInt(ARG_SECTION_NUMBER,1)) {
                1 -> searchResultViewModel.getFollower(ARG_USER).observe(viewLifecycleOwner) {
                    it?.let {
                        when (it) {
                            is UserResult.Loading -> {
                                binding.pbList.visibility = View.VISIBLE
                            }
                            is UserResult.Success -> {
                                binding.pbList.visibility = View.GONE
                                showRecyclerList(it.data)
                            }
                            is UserResult.Error -> {
                                binding.tvErr.text = "Terjadi Kesalahan " + it.error
                            }
                        }
                    }
                }
                2 -> searchResultViewModel.getFollowing(ARG_USER).observe(viewLifecycleOwner) {
                    it?.let {
                        when (it) {
                            is UserResult.Loading -> {
                                binding.pbList.visibility = View.VISIBLE
                            }
                            is UserResult.Success -> {
                                binding.pbList.visibility = View.GONE
                                showRecyclerList(it.data)
                            }
                            is UserResult.Error -> {
                                binding.tvErr.text = "Terjadi Kesalahan " + it.error
                            }
                        }
                    }
                }
            }
        }else{
            binding.apply {
                pbList.visibility= View.GONE
                tvErr.visibility= View.VISIBLE
                tvErr.text = "Tidak Ada Internet"
            }
        }
    }


    private fun showRecyclerList(item: List<ItemsItem?>) {
        val listUserAdapter = ListUserAdapter(item)
        binding.rvListUser.adapter = listUserAdapter
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ItemsItem) {

            }
        })
    }

    companion object {
        const val ARG_SECTION_NUMBER = "section_number"
        var ARG_USER = "arg_user"
        var THEME = "theme"
    }
}