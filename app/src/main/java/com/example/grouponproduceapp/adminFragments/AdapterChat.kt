package com.example.grouponproduceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.databinding.ItemChatMessageBinding
import com.example.grouponproduceapp.userFragments.ChatModel
import com.google.firebase.auth.FirebaseAuth


class AdapterChat(private val messages: List<ChatModel>) : RecyclerView.Adapter<AdapterChat.ChatViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val senderId = messages[position].senderId ?: ""

        return holder.bind(messages[position].message ?: "", senderId)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String, senderId: String) {

            val uid = FirebaseAuth.getInstance().uid ?: ""
            if (senderId == uid) {

                binding.receiverMessageTv.isVisible = false
                binding.senderMessageTv.isVisible = true

                binding.senderMessageTv.text = message
            }else {

                binding.receiverMessageTv.isVisible = true
                binding.senderMessageTv.isVisible = false

                binding.receiverMessageTv.text = message
            }
        }
    }
}
