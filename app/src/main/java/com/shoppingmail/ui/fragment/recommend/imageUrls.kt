package com.shoppingmail.ui.fragment.recommend

class imageUrls {

    private var picture = 0

    fun infoBean(picture: Int) {
        this.picture = picture
    }

    fun getPicture(): Int {
        return picture
    }

    fun setPicture(picture: Int) {
        this.picture = picture
    }

}