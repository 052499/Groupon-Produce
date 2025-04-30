package com.example.grouponproduceapp.adapters

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.models.OrderDetailz
import com.example.grouponproduceapp.models.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class AdapterOrderAdminSummary(private var orders: List<Pair<String, OrderDetailz>>,
                               private val onClickListener: (userId: String, orderId: String) -> Unit)
    : RecyclerView.Adapter<AdapterOrderAdminSummary.OrderViewHolder>() {

    // ViewHolder to bind the data
    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
//        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        var tvChat: TextView = itemView.findViewById(R.id.tvChat)
        val llOrderDetails: LinearLayout = itemView.findViewById(R.id.llOrderDetails)
        val spinnerOrderStatus: Spinner = itemView.findViewById(R.id.spinnerOrderStatus)
        val redDot: View = itemView.findViewById(R.id.redDot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_admin, parent, false)
        return OrderViewHolder(itemView)
    }

    private fun hasUnreadMessages(orderId: String, callback: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val id = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        db.collection("chats")
            .whereEqualTo("order_id", orderId)
            .whereEqualTo("receiver_id", id)
            .whereEqualTo("isRead", false)
            .get()
            .addOnSuccessListener { snapshot ->
                callback(!snapshot.isEmpty)
            }
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val (orderId, order) = orders[position]

        // Set initial order data
        holder.tvOrderId.text = "Order ID: ${orderId}"
        holder.tvOrderDate.text = "Order Date: ${Date(order.orderDate)}"
//        holder.tvOrderStatus.text = "Status: ${order.orderStatus}"

        // Check if there are unread messages for this order
        hasUnreadMessages(order.orderId) { hasUnread ->
            holder.redDot.visibility = if (hasUnread) View.VISIBLE else View.GONE
        }

        holder.tvChat.setOnClickListener {
            onClickListener.invoke(order.userId, orderId)
        }

        // Loop through the products in the order
        holder.llOrderDetails.removeAllViews()
        order.orderDetails.forEach { orderDetail ->
            val productTextView = TextView(holder.itemView.context)
            productTextView.text =
                "Product: ${orderDetail.productName} | Qty: ${orderDetail.productQty} | Price: ${orderDetail.productTotalPrice}"
            holder.llOrderDetails.addView(productTextView)
        }

        // Define status options for the spinner
        val statusOptions = listOf("Processing", "Packed", "Shipped", "Delivered", "Cancelled")

        // Create a custom adapter for the spinner
        val adapter = object : ArrayAdapter<String>(holder.itemView.context, android.R.layout.simple_spinner_item, statusOptions) {
            override fun isEnabled(position: Int): Boolean {
                // Define which options are enabled based on the order status
                return when (order.orderStatus) {
                    "Processing" -> position == 1 || position == 4  // Packed or Cancelled
                    "Packed" -> position == 2  // Shipped
                    "Shipped" -> position == 3  // Delivered
                    else -> false  // All other statuses are allowed
                }
            }

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                // Set color for disabled items
                if (!isEnabled(position)) {
                    textView.setTextColor(Color.GRAY)  // Disabled option
                } else {
                    textView.setTextColor(Color.BLACK)  // Enabled option
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                // Set color for disabled items
                if (!isEnabled(position)) {
                    textView.setTextColor(Color.GRAY)  // Disabled option
                } else {
                    textView.setTextColor(Color.BLACK)  // Enabled option
                }
                return view
            }
        }

        // Set the custom adapter to the spinner
        holder.spinnerOrderStatus.adapter = adapter

        // Set the current order status in the spinner
        val currentStatusPosition = statusOptions.indexOf(order.orderStatus)
        holder.spinnerOrderStatus.setSelection(currentStatusPosition)

        // Handle the item selected event
        holder.spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newStatus = statusOptions[position]
                updateOrderStatus(orderId, newStatus)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }

    override fun getItemCount(): Int = orders.size

    fun setOrders(orders: List<Pair<String, OrderDetailz>>) {
        this.orders = orders
        notifyDataSetChanged()
    }

    private fun updateOrderStatus(orderId: String?, newStatus: String) {
        if (orderId == null) return

        val db = FirebaseFirestore.getInstance()
        val orderRef = db.collection("orders").document(orderId)

        orderRef.get().addOnSuccessListener { documentSnapshot ->
            val order = documentSnapshot.toObject(OrderDetailz::class.java)

            if (order != null && order.orderStatus != newStatus) {
                if (newStatus == "Cancelled") {
                    // If status is changing to "Packed", update stock
                    order.orderDetails?.forEach { orderDetail ->
                        val productRef =
                            FirebaseDatabase.getInstance().getReference("Admins/products")
                                .child(orderDetail.productId)

                        productRef.get().addOnSuccessListener { productSnapshot ->
                            val product = productSnapshot.getValue(Product::class.java)

                            if (product != null) {
//                                Log.d("UOS-UOS=UOS-UOS-4a", "${orderDetail.productQty}   ${product.productStock}")
                                val newStock = (product.productStock ?: 0) + orderDetail.productQty
//                                Log.d("UOS-UOS-UOS-UOS-4", newStock.toString())
                                productRef.child("productStock").setValue(newStock)
                                    .addOnSuccessListener {
                                        Log.d("UOS-UOS-UOS-UOS-5", "Product stock updated to $newStock")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("UOS-UOS-UOS-UOS-Error1", "Error updating product stock: ${exception.message}")
                                    }
                            } else {
                                Log.e("UOS-UOS-UOS-UOS-Error", "Product not found in Realtime Database")
                            }
                        }.addOnFailureListener { exception ->
                            Log.e("UOS-UOS-UOS-UOS-Error2", "Error fetching product: ${exception.message}")
                        }
                    }
                }
                orderRef.update("orderStatus", newStatus)
                    .addOnSuccessListener {
                        Log.d("AdminVM", "Order status updated successfully to $newStatus")
                    }
                    .addOnFailureListener { e ->
                        Log.e("AdminVM", "Error updating order status: ${e.message}")
                    }
            }
        }
    }
}
