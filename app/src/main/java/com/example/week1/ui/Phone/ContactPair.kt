package com.example.week1.ui.Phone

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ContactPair(
    val contact1: @RawValue ContactsData,
    val contact2: @RawValue ContactsData?
) : Parcelable


