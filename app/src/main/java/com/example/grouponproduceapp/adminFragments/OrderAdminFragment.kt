package com.example.grouponproduceapp.adminFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.adapters.AdapterOrderAdminSummary
import com.example.grouponproduceapp.databinding.FragmentOrderAdminBinding
import com.example.grouponproduceapp.viewmodels.AdminVM
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class OrderAdminFragment : Fragment() {

    private val adminVM: AdminVM by viewModels()
    private lateinit var adapter: AdapterOrderAdminSummary
    private var adminId: String? = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentOrderAdminBinding.inflate(inflater, container, false)

        adapter = AdapterOrderAdminSummary(mutableListOf()) {  userId, orderId ->

        val bundle = Bundle()
        bundle.putString("orderId", orderId)
        bundle.putString("userId", userId)

            findNavController().navigate(R.id.action_global_admin_userChatFragment, bundle)
    }

        binding.rvAdminOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAdminOrders.adapter = adapter

        // Fetch orders for the admin
        adminId?.let {
            lifecycleScope.launch {
                Log.d("OAF-onCreateView1", adminId.toString())
                adminVM.fetchOrdersForAdmin(it).collect { orders ->
                    Log.d("OAF-onCreateView2", "Orders are:  $orders")
                    if (orders.isEmpty()){
                        Log.d("OAF-onCreateView3", "found no orders.")
                        binding.rvAdminOrders.visibility = View.GONE
                        binding.tvNoOrders.visibility = View.VISIBLE
                    }
                    else {
                        Log.d("OAF-onCreateView3", "found orders.")
                        binding.rvAdminOrders.visibility = View.VISIBLE
                        binding.tvNoOrders.visibility = View.GONE
                    }

                    val filteredOrders = orders.filter { (_, order) ->
                        order.orderDetails?.any { it.adminId == adminId } == true
                    }
//                    Log.d("OAF-2onCreateView", orders.toString())
                    adapter.setOrders(filteredOrders)
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        print("onResume")
        requireActivity().findViewById<View>(R.id.bottomMenu)?.visibility = View.VISIBLE
    }
}