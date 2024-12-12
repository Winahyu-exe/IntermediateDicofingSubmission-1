package com.dicoding.picodiploma.loginwithanimation.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.bumptech.glide.Glide


class StoryAdapter : ListAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(StoryDiffCallback()) {

    // ViewHolder untuk tiap item cerita
    inner class StoryViewHolder(private val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            // Mengisi data ke tampilan
            binding.itemTitleTextView.text = story.name
            binding.itemDescriptionTextView.text = story.description

            // Menggunakan Glide untuk menampilkan gambar
            Glide.with(binding.itemImageView.context)
                .load(story.photoUrl)
                .into(binding.itemImageView)
        }
    }

    // Inflating layout dan menghubungkan ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    // Mengikat data ke ViewHolder
    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        holder.bind(story)
    }
}

// DiffCallback untuk membandingkan data cerita
class StoryDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<ListStoryItem>() {
    override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem.id == newItem.id // Cek dengan ID yang unik
    }

    override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
        return oldItem == newItem
    }
}
