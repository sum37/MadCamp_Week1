package com.example.week1.ui.Avatar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AvatarViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text

    private val _color = MutableLiveData<Int>()
    val color: LiveData<Int> = _color

    private val _glasses = MutableLiveData<String>()
    val glasses: LiveData<String> = _glasses

    private val _hat = MutableLiveData<String>()
    val hat: LiveData<String> = _hat

    private val _cloth = MutableLiveData<String>()
    val cloth: LiveData<String> = _cloth

    private val _background = MutableLiveData<String>()
    val background: LiveData<String> = _background

    fun setColor(selectedColor: Int) {
        _color.value = selectedColor
        Log.d("color입니다", "$selectedColor")
    }

    fun setGlasses(selectedGlasses: String) {
        _glasses.value = selectedGlasses
    }

    fun setHat(selectedHat: String) {
        _hat.value = selectedHat
    }

    fun setCloth(selectedCloth: String) {
        _cloth.value = selectedCloth
    }

    fun setBackground(selectedBackground: String) {
        _background.value = selectedBackground
    }
}
