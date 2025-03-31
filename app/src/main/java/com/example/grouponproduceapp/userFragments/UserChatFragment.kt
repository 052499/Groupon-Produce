package com.example.grouponproduceapp.userFragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.adapters.AdapterChat
import com.example.grouponproduceapp.databinding.FragmentUserChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserChatFragment : Fragment() {
    private var _binding: FragmentUserChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterChat: AdapterChat

    private var userId = ""
    private val userName = ""
    private var orderId = ""
    private var allChat: MutableList<ChatModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserChatBinding.inflate(inflater, container, false)
        orderId = arguments?.getString("orderId").orEmpty()
        userId = arguments?.getString("userId").orEmpty()

        binding.toolBarChatFragment.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        statusBarColor()
        setupRecyclerView()
        getAllChat()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        recyclerView = binding.recyclerViewMessages
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapterChat = AdapterChat(allChat)
        recyclerView.adapter = adapterChat
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChat() {
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.GONE
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("chats")
            .whereIn("sender_id", listOf(id, userId))
            .whereIn("receiver_id", listOf(id, userId))
            .whereEqualTo("order_id", orderId)
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) return@addSnapshotListener
                
                allChat.clear()
                querySnapshot?.documents?.mapNotNullTo(allChat) { document ->
                    val data = document.data ?: return@mapNotNullTo null
                    val timestamp = data["timestamp"]
                    val dateInMillis = when (timestamp) {
                        is com.google.firebase.Timestamp -> timestamp.seconds * 1000
                        is Long -> timestamp
                        else -> 0L
                    }
                    ChatModel(
                        id = document.id,
                        date = dateInMillis,
                        message = data["message"] as? String ?: "",
                        senderId = data["sender_id"] as? String ?: "",
                        receiverId = data["receiver_id"] as? String ?: ""
                    )
                }

                allChat.sortBy { it.date }
                adapterChat.notifyDataSetChanged()
                recyclerView.post { recyclerView.scrollToPosition(adapterChat.itemCount - 1) }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            val myTimeStamp = System.currentTimeMillis()
            val user = FirebaseAuth.getInstance().currentUser ?: return

            val params = hashMapOf(
                "message" to message,
                "sender_id" to user.uid,
                "sender_name" to user.displayName.orEmpty(),
                "receiver_id" to userId,
                "receiver_name" to userName,
                "order_id" to orderId,
                "timestamp" to myTimeStamp
            )

            FirebaseFirestore.getInstance().collection("chats").add(params)
                .addOnSuccessListener {
                    getAllChat()
                    binding.etMessage.setText("")
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}

data class ChatModel(
    var id: String? = null,
    var message: String? = null,
    var date: Long,
    var senderId: String? = null,
    var senderName: String? = null,
    var receiverId: String? = null,
    var receiverName: String? = null
)
