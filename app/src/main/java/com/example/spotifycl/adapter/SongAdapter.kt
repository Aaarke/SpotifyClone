package com.example.spotifycl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.spotifycl.R
import com.example.spotifycl.model.Song
import kotlinx.android.synthetic.main.list_item.view.*
import javax.inject.Inject

class SongAdapter @Inject constructor(private val glide: RequestManager) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.mediaId == newItem.mediaId
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    var songs: List<Song>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item, null))
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.itemView.apply {
            tvPrimary.text = song.title
            tvSecondary.text = song.subtitle
            glide.load(song.imageUrl).into(ivItemImage)
            setOnClickListener {
                onItemClickedListeners?.invoke(song)
            }

        }
    }

    private var onItemClickedListeners: ((Song) -> Unit)? = null

    fun setOnItemClickedListeners(listeners: (Song) -> Unit) {
        onItemClickedListeners = listeners
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}