package com.example.week1.ui.Phone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactsData(
    val id: Int,
    val rawContactId: Int, // 추가
    val name: String,
    val number: String
) : Parcelable
