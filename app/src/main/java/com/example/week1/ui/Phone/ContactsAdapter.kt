package com.example.week1.ui.Phone

import android.telephony.PhoneNumberUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.week1.databinding.ViewContactsListBinding
import java.util.Locale


class ContactsAdapter(private val contactsList: ArrayList<ContactsData>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ViewContactsListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount() = contactsList.size

    inner class ViewHolder(private val binding: ViewContactsListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.txtName.text = contactsList[adapterPosition].name
            binding.txtPhoneNumber.text = PhoneNumberUtils.formatNumber(contactsList[adapterPosition].number, Locale.getDefault().country)
        }
    }
}