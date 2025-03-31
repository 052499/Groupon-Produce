package com.example.grouponproduceapp.userFragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.adapters.AdapterChat
import com.example.grouponproduceapp.databinding.FragmentUserChatBinding

import androidx.fragment.app.Fragment
import com.example.grouponproduceapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserChatFragment : Fragment() {
    private var _binding: FragmentUserChatBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterChat: AdapterChat

    //private var messageList = mutableListOf<String>() // Store the chat messages

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

        binding.toolBarChatFragment.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }

        statusBarColor()
        getAllChat()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        print("onResume")
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getAllChat() {
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.GONE
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("chats")
            .whereIn("sender_id", listOf(id, userId))
            .whereIn("receiver_id", listOf(id, userId))
            .whereEqualTo("order_id", orderId)

        query.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            allChat.clear()

            querySnapshot?.documents?.forEach { document ->

                val data = document.data ?: return@forEach
                val timestamp = data["timestamp"]
                val dateInMillis = when (timestamp) {
                    is com.google.firebase.Timestamp -> timestamp.seconds * 1000 // Convert to milliseconds
                    is Long -> timestamp // If already stored as Long
                    else -> 0L // Default value
                }

                val chat = ChatModel(
                    id = document.id,
                    date = dateInMillis,
                    message = data["message"] as? String ?: "",
                    senderId = data["sender_id"] as? String ?: "",
                    receiverId = data["receiver_id"] as? String ?: ""
                )

                allChat.add(chat)
            }

             allChat = allChat.sortedBy { it.date }.toMutableList()

            adapterChat = AdapterChat(allChat)
            recyclerView.adapter = adapterChat

            adapterChat.notifyDataSetChanged()
            recyclerView.post {
                recyclerView.scrollToPosition(adapterChat.itemCount - 1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up RecyclerView
        recyclerView = binding.recyclerViewMessages
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

//        recyclerView.setHasFixedSize(true)
        //messageList = getSampleMessages()


        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()

            if (message.isNotEmpty()) {

                val myTimeStamp = System.currentTimeMillis() // Convert to seconds

                val user = FirebaseAuth.getInstance().currentUser
                val id = user?.uid ?: ""
                val name = user?.displayName ?: ""

                val params = hashMapOf(
                    "message" to message,
                    "sender_id" to id,
                    "sender_name" to name,
                    "receiver_id" to userId,
                    "receiver_name" to userName,
                    "order_id" to orderId,
                    "timestamp" to myTimeStamp
                )

                val db = FirebaseFirestore.getInstance()
                val path = "chats"

                db.collection(path).add(params)
                    .addOnSuccessListener {
                        getAllChat()
                        binding.etMessage.setText("")
                    }
                    .addOnFailureListener {
                    }
            }
        }
    }

    private fun getSampleMessages(): MutableList<String> {
        return listOf("").toMutableList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(
                requireContext(),
                R.color.blue )
            statusBarColor = statusBarColors
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
