package com.shoppingmail.ui.object_class

class Cart_Goods (
        val productItemsId: Long,
        val productItemsName: String,
        val productItemsTitle :String,
        val productItemsPrice:Double,
        val productItemsDetailImage:String,

        var number: Int = 1,
        val type: String = "无",
        var isSelect:Boolean = false)