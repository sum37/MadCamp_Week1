package com.example.week1.ui.Avatar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AvatarViewModel : ViewModel() {

    private val _color = MutableLiveData<Int>()
    val color: LiveData<Int> = _color

    private val _glasses = MutableLiveData<Int>()
    val glasses: LiveData<Int> = _glasses

    private val _hat = MutableLiveData<Int>()
    val hat: LiveData<Int> = _hat

    private val _cloth = MutableLiveData<Int>()
    val cloth: LiveData<Int> = _cloth

    private val _background = MutableLiveData<Int>()
    val background: LiveData<Int> = _background

    fun setColor(selectedColor: Int) {
        _color.value = selectedColor
        Log.d("color입니다", "${selectedColor}")
    }

    fun setGlasses(selectedGlasses: Int) {
        _glasses.value = selectedGlasses
    }

    fun setHat(selectedHat: Int) {
        _hat.value = selectedHat
    }

    fun setCloth(selectedCloth: Int) {
        _cloth.value = selectedCloth
    }

    fun setBackground(selectedBackground: Int) {
        _background.value = selectedBackground
    }
}
