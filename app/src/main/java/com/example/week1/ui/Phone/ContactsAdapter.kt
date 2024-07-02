package com.example.week1.ui.Phone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.databinding.ViewContactItemBinding

class ContactsAdapter(
    private val contacts: List<ContactsData>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(contact: ContactsData)
    }

    inner class ContactViewHolder(val binding: ViewContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: ContactsData) {
            binding.contact = contact
            binding.executePendingBindings()
        }

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(contacts[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ViewContactItemBinding.inflate(layoutInflater, parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(contacts[position])
    }

    override fun getItemCount() = contacts.size
}
