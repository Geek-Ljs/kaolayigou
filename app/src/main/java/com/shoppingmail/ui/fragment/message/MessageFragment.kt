package com.shoppingmail.ui.fragment.message

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.shoppingmail.R
import com.shoppingmail.ui.fragment.recommend.RecommendViewModel

class MessageFragment : Fragment() {

    companion object {
        fun newInstance() = MessageFragment()
    }

    private lateinit var messageViewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        messageViewModel =
//            ViewModelProvider(this).get(MessageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_message, container, false)
//        val textView : TextView = root.findViewById(R.id.text_message)
//        messageViewModel.text.observe(viewLifecycleOwner, {
//            textView.text = it
//        })
        return root

//        return inflater.inflate(R.layout.fragment_message, container, false)
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        viewModel = ViewModelProvider(this).get(MessageViewModel::class.java)
//        // TODO: Use the ViewModel
//    }

}