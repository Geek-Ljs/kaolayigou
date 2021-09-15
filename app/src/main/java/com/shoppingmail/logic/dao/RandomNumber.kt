package com.shoppingmail.logic.dao

/**
 *   author ： Jason
 *   time    ： 2021/3/11
 */
class RandomNumber {
    /**
     * 用于生成验证码
     * 6位随机数
     */

    fun getRandomNumber():Long{
        return ((Math.random()*9*Math.pow(10.0, 5.0))).toLong() + Math.pow(10.0, 5.0).toLong()
    }
}