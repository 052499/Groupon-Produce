package com.example.grouponproduceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.databinding.ItemChatMessageBinding
import com.example.grouponproduceapp.userFragments.ChatModel
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messageList: List<ChatModel>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatMessageBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(messageList[position])
    }

    override fun getItemCount(): Int = messageList.size

    class ChatViewHolder(private val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        private val currentUserId = FirebaseAuth.getInstance().uid.orEmpty()

        fun bind(chat: ChatModel) {
            val isSender = chat.senderId == currentUserId
            val message = chat.message.orEmpty()

            binding.senderMessageTv.isVisible = isSender
            binding.receiverMessageTv.isVisible = !isSender

            if (isSender) {
                binding.senderMessageTv.text = message
            } else {
                binding.receiverMessageTv.text = message
            }
        }
    }
}
