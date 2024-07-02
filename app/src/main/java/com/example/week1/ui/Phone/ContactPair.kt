package com.example.week1.ui.Phone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactPair(
    val contact1: ContactsData,
    val contact2: ContactsData?
) : Parcelable