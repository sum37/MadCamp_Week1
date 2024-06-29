package com.example.week1

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.databinding.ItemImageBinding

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val imageUris = mutableListOf<String>()

    fun submitList(images: List<String>) {
        imageUris.clear()
        imageUris.addAll(images)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUris[position])
    }

    override fun getItemCount(): Int = imageUris.size

    class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: String) {
            val context = binding.root.context
            val inputStream = context.contentResolver.openInputStream(Uri.parse(uri))
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.imageView.setImageBitmap(bitmap)
        }
    }
}

