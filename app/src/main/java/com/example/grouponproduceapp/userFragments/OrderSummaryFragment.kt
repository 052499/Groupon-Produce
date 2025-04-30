


package com.example.grouponproduceapp.userFragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.activity.UsersMainActivity
import com.example.grouponproduceapp.adapters.AdapterOrderSummary
import com.example.grouponproduceapp.databinding.FragmentOrderSummaryBinding
import com.example.grouponproduceapp.models.OrderDetailz
import com.example.grouponproduceapp.models.OrderedItemDetails
import com.example.grouponproduceapp.viewmodels.UserVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class  OrderSummaryFragment : Fragment() {
    private lateinit var binding: FragmentOrderSummaryBinding
    private lateinit var adapterOrderedItems: AdapterOrderSummary
    private lateinit var firestore: FirebaseFirestore
    private val viewModel: UserVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        statusBarColor()

        firestore = FirebaseFirestore.getInstance()
        (activity as? UsersMainActivity)?.hideCheckoutButton()
        setupOrderSummary()
        binding.btnContinueShopping.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        return binding.root
    }

    private fun openFullScreenFragment() {
        val fullScreenFragment = UserChatFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out) // Optional animation
            .replace(android.R.id.content, fullScreenFragment) // Ensures full-screen display
            .addToBackStack(null) // Enables back navigation
            .commit()
    }

    private fun setupOrderSummary() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val orderDetails = mutableListOf<OrderDetailz>()
            binding.rvOrderedItems.layoutManager = LinearLayoutManager(requireContext())

            adapterOrderedItems = AdapterOrderSummary(orderDetails){ adminId, orderId ->

                val bundle = Bundle()
                bundle.putString("orderId", orderId)
                bundle.putString("userId", adminId)

                findNavController().navigate(R.id.action_global_user_userChatFragment, bundle)
            }
            binding.rvOrderedItems.adapter = adapterOrderedItems

            lifecycleScope.launch {
                val orderedItems = fetchOrderedItemsFromFirestore(userId)
                    .sortedByDescending { it.orderDate }
                if (orderedItems.isEmpty()){
                    binding.rvOrderedItems.visibility = View.GONE
                    binding.tvNoOrders.visibility = View.VISIBLE
                }
                else {
                    binding.rvOrderedItems.visibility = View.VISIBLE
                    binding.tvNoOrders.visibility = View.GONE
                }
                val deferreds = orderedItems.map { orderedItem ->
                    async {
                        try {
                            val orderedWithItemsDetails = OrderDetailz(
                                orderId = orderedItem.orderId,
                                userId = userId,
//                                adminId = orderedItem.adminId,
                                orderPrice = orderedItem.orderPrice,
                                orderDate = orderedItem.orderDate,
                                orderStatus = orderedItem.orderStatus,
                                orderDetails = orderedItem.orderDetails.map { detail ->
                                    OrderedItemDetails(
                                        orderId = orderedItem.orderId,
                                        productId = detail.productId,
                                        productName = detail.productName,
                                        productQty = detail.productQty,
                                        productTotalPrice = detail.productTotalPrice,
                                        productImgUri = detail.productImgUri,
                                        adminId= detail.adminId
                                    )
                                }
                            )
                            orderDetails.add(orderedWithItemsDetails)
                        } catch (e: Exception) {
                            Log.e("OrderSummaryFragment", "Error fetching product: ${e.message}")
                        }
                    }
                }

                deferreds.awaitAll()
                if (orderDetails.isNotEmpty()) {
                    adapterOrderedItems.setCartItems(orderDetails)
                } else {
                    Log.e("OrderSummaryFragment", "No products found for cart.")
                }
            }
        } else {
            Log.e("OrderSummaryFragment", "User not logged in.")
        }
    }

    private suspend fun fetchOrderedItemsFromFirestore(userId: String): List<OrderDetailz> {
        return try {
            val ordersRef = firestore.collection("orders")
            // Query to fetch orders where the userId matches the current user's userId
            val snapshot = ordersRef
                .whereEqualTo("userId", userId)
                .get()
                .await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(OrderDetailz::class.java)
            }
        } catch (e: Exception) {
            Log.e("OrderSummaryFragment", "Error fetching ordered items from Firestore: ${e.message}")
            emptyList()
        }
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(
                requireContext(),
                R.color.defaultBackground )
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
