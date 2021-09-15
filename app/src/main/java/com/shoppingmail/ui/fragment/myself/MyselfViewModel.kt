package com.shoppingmail.ui.fragment.myself

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyselfViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is MySelf Fragment"
    }
    val text: LiveData<String> = _text
}