package com.shoppingmail.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.shoppingmail.*
import com.shoppingmail.ui.object_class.Address
import com.shoppingmail.ui.object_class.OrderComfirm

/**
 *   author ： Jason
 *   time    ： 2021/4/5
 */
class AddressAdapter(val addressList:List<Address>, val itemList:List<OrderComfirm>, val activity: AddressActivity) :
//class AddressAdapter(val addressList:List<Address>, val itemList:List<OrderComfirm>) :
        RecyclerView.Adapter<AddressAdapter.ViewHolder>(){

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val addressTip : TextView = view.findViewById(R.id.address_tips)
        val addressName : TextView = view.findViewById(R.id.submit_address_name)
        val addressTelephone : TextView = view.findViewById(R.id.submit_address_phone)
        val addressAddr : TextView = view.findViewById(R.id.submit_address_addr)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.address_item, parent, false)
        val viewHolder = ViewHolder(view)
        //跳转到订单
//        if (flag == 1){
            viewHolder.itemView.setOnClickListener {
                val position = viewHolder.adapterPosition
                val addr = addressList[position]
                val intent = Intent(parent.context, ComfirmOrderActivity::class.java)
                intent.putExtra("address_id", addr.addressId)
                intent.putExtra("address_user_name", addr.addressUserName)
                intent.putExtra("address_user_telephone", addr.addressUserTelephone)
                intent.putExtra("address_user_addr", addr.addressUserAddr)
                if (!itemList.isEmpty())
                    intent.putExtra("itemList_data", itemList as ArrayList<OrderComfirm>)
                parent.context.startActivity(intent)
                activity.finish()
            }
//        }
//        else if (flag == 2){
//            val position = viewHolder.adapterPosition
//            val addr = addressList[position]
//            val intent = Intent(parent.context, ComfirmOrderActivity::class.java)
//            intent.putExtra("address_id", addr.addressId)
//            intent.putExtra("address_user_name", addr.addressUserName)
//            intent.putExtra("address_user_telephone", addr.addressUserTelephone)
//            intent.putExtra("address_user_addr", addr.addressUserAddr)
//            intent.putExtra("itemList_data", itemList as ArrayList<OrderComfirm>)
//            parent.context.startActivity(intent)
//            activity.finish()
//        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: AddressAdapter.ViewHolder, position: Int) {
        val addr = addressList[position]
        holder.addressTip.text = addr.addressUserName[0].toString()
        holder.addressName.text = addr.addressUserName
        holder.addressTelephone.text = addr.addressUserTelephone
        holder.addressAddr.text = addr.addressUserAddr
    }

    override fun getItemCount() = addressList.size
}