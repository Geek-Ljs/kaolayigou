package com.shoppingmail.logic.dao

import android.util.Log
import java.sql.*

/**
 *   author ： Jason
 *   time    ： 2021/3/8
 */
class DatabaseOperations {
    private val keyt:String = "LQMSHW"
    private lateinit var cn : Connection
    private lateinit var ps : PreparedStatement
    private lateinit var rs:ResultSet
    private val userName = "root"
    private val password = "0987654321"

    //数据库的连接
    fun mysqlConnect(): Connection? {
        try {
            //加载驱动
            Class.forName("com.mysql.jdbc.Driver")

            //建立连接，模拟器的ip填：10.0.2.2，真机上填172.0.0.1/localhost
            //androidStudio自带模拟器
//            cn = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/shoppingmall",
//                "root", "123456")
            cn = DriverManager.getConnection("jdbc:mysql://119.29.120.242:3306/shoppingmall",
                    userName, password)
            //连接到真机
//            cn = DriverManager.getConnection("jdbc:mysql://192.168.43.201:3306/shoppingmall",
//                "root", "123456")
            Log.d("MainActivity", cn.toString())
            return cn
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }

    //数据的查询

    //(1）登录验证
//       1) 点击获取验证码按钮
    fun loginVerificationButton(email_account:String):Boolean{
//        val sql:String = "Select * From members Where user_members_email=?"
        val sql:String = "Select * From members"
        Log.d("邮箱", email_account)
        try{
            var flag=0
            Thread(Runnable {
                cn = DatabaseOperations().mysqlConnect()!!
                ps = cn.prepareStatement(sql)
//                ps.setString(1, email_account)
                rs = ps.executeQuery()
                if (!rs.wasNull()){
                    flag = 1
                }
//                Log.d("行数：", rs.row.toString())
//                Log.d("行数：", rs.wasNull().toString())
                closeConnect()
            }).start()
            if (flag==1){
                return true
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return false
    }
//        2）点击登录注册按钮
//    fun loginAndRegisterButtom(email_account: String):Boolean{
//        return false
//    }
    //数据的插入


    //数据的更新


    //数据的删除


    //返回密钥
    fun returnKey():String{
        return keyt
    }

    //关闭连接
    private fun closeConnect() {
        cn.close()
        ps.close()
        rs.close()
    }
}