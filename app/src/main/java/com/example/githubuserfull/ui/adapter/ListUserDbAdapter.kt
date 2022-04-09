package com.example.githubuserfull.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.githubuserfull.data.client.entity.UserEntity
import com.example.githubuserfull.databinding.LayoutListUserBinding

class ListUserDbAdapter(
    private val listUser: List<UserEntity?>
) : RecyclerView.Adapter<ListUserDbAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            LayoutListUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.binding.apply {
            tvUsername.text = listUser[position]?.login
        }
        listUser[position]?.avatar.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .transform(CircleCrop())
                .into(holder.binding.ivAvatar)
        }
        holder.itemView.setOnClickListener {
            listUser[holder.adapterPosition]?.let { onItemClickCallback.onItemClicked(it) }
        }
    }

    override fun getItemCount(): Int = listUser.size
    inner class ListViewHolder(var binding: LayoutListUserBinding) : RecyclerView.ViewHolder(binding.root)
    interface OnItemClickCallback {
        fun onItemClicked(data: UserEntity)
    }
}