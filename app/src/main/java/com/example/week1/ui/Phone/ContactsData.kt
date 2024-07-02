package com.example.week1.ui.Phone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import android.net.Uri


@Parcelize
data class ContactsData(
    val id: Int,
    val name: String,
    val number: String,
    val photoUri: Uri? //
) : Parcelable

