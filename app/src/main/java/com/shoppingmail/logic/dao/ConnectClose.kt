package com.shoppingmail.logic.dao

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 *   author ： Jason
 *   time    ： 2021/3/8
 */

fun close(cn:Connection, ps:PreparedStatement, rs:ResultSet){
    if (cn != null){
        cn.close()
    }
    if (ps !=null){
        ps.close()
    }
    if (rs != null){
        rs.close()
    }
}