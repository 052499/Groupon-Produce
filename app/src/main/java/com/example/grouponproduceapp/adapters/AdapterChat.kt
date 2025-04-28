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
        val message = messages[position]
        val senderId = message.senderId ?: ""
        val uid = FirebaseAuth.getInstance().uid ?: ""

        holder.bind(message, senderId)

//        // Check read status and show the appropriate "read receipt" only on the sender's side
//        if (senderId == uid) {
//            // Sender's side: Check if the receiver has read the message
//            if (message.readStatus?.get(message.receiverId) == true) {
//                holder.binding.readReceiptTv.isVisible = true
//            } else {
//                holder.binding.readReceiptTv.isVisible = false
//            }
//        } else {
//            // Receiver's side: Don't show the read receipt
//            holder.binding.readReceiptTv.isVisible = false
//        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ChatViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatModel, senderId: String) {
            val uid = FirebaseAuth.getInstance().uid ?: ""

            if (senderId == uid) {
                binding.receiverMessageTv.isVisible = false
                binding.senderMessageTv.isVisible = true
                binding.senderMessageTv.text = message.message

                // Only show the read receipt on the sender's side
//                if (message.readStatus?.get(message.receiverId) == true) {
//                    binding.readReceiptTv.isVisible = true
//                } else {
//                    binding.readReceiptTv.isVisible = false
//                }

            } else {
                binding.receiverMessageTv.isVisible = true
                binding.senderMessageTv.isVisible = false
                binding.receiverMessageTv.text = message.message

                // Hide the read receipt for the receiver
//                binding.readReceiptTv.isVisible = false
            }

            // Show or hide the read receipt based on the readStatus
//            if (message.readStatus?.get(uid) == true) {
//                binding.readReceiptTv.isVisible = true
//            } else {
//                binding.readReceiptTv.isVisible = false
//            }
        }
    }
}
