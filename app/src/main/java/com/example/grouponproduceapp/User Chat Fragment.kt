package com.example.grouponproduceapp.userFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.adapters.AdapterChat
import com.example.grouponproduceapp.databinding.FragmentUserChatBinding

class UserChatFragment : DialogFragment() {
    private var _binding: FragmentUserChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterChat: AdapterChat

    private var messageList = mutableListOf<String>() // Store the chat messages

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up RecyclerView
        recyclerView = binding.recyclerViewMessages
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        recyclerView.setHasFixedSize(true)
        messageList = getSampleMessages()
        adapterChat = AdapterChat(messageList)
        recyclerView.adapter = adapterChat

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                messageList.add(message)
                adapterChat.notifyItemInserted(messageList.size - 1)
                recyclerView.scrollToPosition(messageList.size - 1)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun getSampleMessages(): MutableList<String> {
        return listOf(
            "Hello, how can I help you?",
        ).toMutableList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}