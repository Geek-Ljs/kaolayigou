package com.shoppingmail.logic.dao

/**
 *   author ： Jason
 *   time    ： 2021/3/12
 */

fun main(){
    var str = "1ab344"
//    for (i in str){
////        if (i.isDigit()){
////            println("isDigit")
////        }
////        else if (i.isLetter()){
////            println("isLetter")
////        }
//        if (!i.isLetterOrDigit()){
//            println("Yes")
//        }
//    }
    if (str.length<4 || str.length>16){
        println("Yes")
    }
}