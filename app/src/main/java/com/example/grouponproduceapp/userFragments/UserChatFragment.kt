package com.example.grouponproduceapp.userFragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
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

    private val handler = Handler()
    private lateinit var refreshRunnable: Runnable

    var userId = ""
    val userName = ""
    var orderId = ""
    var allChat: MutableList<ChatModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserChatBinding.inflate(layoutInflater)

        orderId = arguments?.getString("orderId").toString()
        userId = arguments?.getString("userId").toString()

        binding.toolBarChatFragment.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        statusBarColor()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerViewMessages
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapterChat = AdapterChat(allChat)
        recyclerView.adapter = adapterChat

        getAllChat()
        markMessagesAsRead(orderId)

        // 🟡 Auto-refresh every 1 second
        refreshRunnable = object : Runnable {
            override fun run() {
                getAllChat()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(refreshRunnable)

        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.GONE
    }

    private fun markMessagesAsRead(orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val chatRef = db.collection("chats")
            .whereEqualTo("order_id", orderId)
            .whereEqualTo("receiver_id", id)
            .whereEqualTo("isRead", false)

        chatRef.get().addOnSuccessListener { documents ->
            for (document in documents) {
                document.reference.update("isRead", true)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChat() {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val query = db.collection("chats")
            .whereIn("sender_id", listOf(id, userId))
            .whereIn("receiver_id", listOf(id, userId))
            .whereEqualTo("order_id", orderId)

        query.addSnapshotListener { querySnapshot, error ->
            if (error != null || querySnapshot == null) return@addSnapshotListener

            val updatedList = mutableListOf<ChatModel>()

            for (document in querySnapshot.documents) {
                val data = document.data ?: continue
                val timestamp = data["timestamp"]
                val dateInMillis = when (timestamp) {
                    is com.google.firebase.Timestamp -> timestamp.seconds * 1000
                    is Long -> timestamp
                    else -> 0L
                }

                val chat = ChatModel(
                    id = document.id,
                    date = dateInMillis,
                    message = data["message"] as? String ?: "",
                    senderId = data["sender_id"] as? String ?: "",
                    receiverId = data["receiver_id"] as? String ?: "",
                    readStatus = data["readStatus"] as? Map<String, Boolean>
                )

                if (chat.receiverId == id && chat.readStatus?.get(id) == false) {
                    db.collection("chats").document(chat.id!!).update("readStatus.$id", true)
                }

                updatedList.add(chat)
            }

            allChat.clear()
            allChat.addAll(updatedList.sortedBy { it.date })

            adapterChat.notifyDataSetChanged()
            recyclerView.post {
                recyclerView.scrollToPosition(adapterChat.itemCount - 1)
            }
        }
    }

    private fun sendMessage(message: String) {
        val myTimeStamp = System.currentTimeMillis()
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val id = user.uid
        val name = user.displayName ?: ""

        val params = hashMapOf(
            "message" to message,
            "sender_id" to id,
            "sender_name" to name,
            "receiver_id" to userId,
            "receiver_name" to userName,
            "order_id" to orderId,
            "timestamp" to myTimeStamp,
            "isRead" to false,
            "readStatus" to hashMapOf(
                id to false,
                userId to false
            )
        )

        FirebaseFirestore.getInstance().collection("chats")
            .add(params)
            .addOnSuccessListener {
                binding.etMessage.setText("")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable) // 🧹 Stop auto-refresh
        _binding = null
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.blue)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
