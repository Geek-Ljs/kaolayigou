package com.shoppingmail.ui.fragment.recommend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 *   author ： Jason
 *   time    ： 2021/3/5
 */
class RecommendViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Recommend Fragment"
    }
    val text:LiveData<String> = _text
}