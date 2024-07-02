package com.example.week1.ui.Phone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ContactsData(
    val id: @RawValue Int,
    val name: @RawValue String,
    val number: @RawValue String
) : Parcelable
