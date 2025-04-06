package com.example.grouponproduceapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.ItemVuOrderSummaryBinding
import com.example.grouponproduceapp.databinding.ItemVuOrderSummaryDetailsBinding
import com.example.grouponproduceapp.models.OrderDetailz
import com.example.grouponproduceapp.models.OrderedItemDetails
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdapterOrderSummary(
    private val orderedItems: MutableList<OrderDetailz>,
    private val onClickListener: (adminId: String, orderId: String) -> Unit
) : RecyclerView.Adapter<AdapterOrderSummary.VH>() {

    class VH(val binding: ItemVuOrderSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemVuOrderSummaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = orderedItems[position]

        // Display order details
        val formattedDate = formatDate(item.getOrderDateAsDate())
        holder.binding.tvOrderStatus.text = item.orderStatus
        holder.binding.tvOrderDate.text = formattedDate

        // Setup RecyclerView to display product details
        //val adapter = ProductDetailsAdapter(item.orderDetails)
        val adapter = ProductDetailsAdapter(item.orderDetails){ adminId ->

            val orderId = item.orderId
            onClickListener.invoke(adminId, orderId)
        }
        holder.binding.rvOrderedItems.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.rvOrderedItems.adapter = adapter
    }

    fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ) // Customize the format as needed
        return dateFormat.format(date)
    }

    override fun getItemCount(): Int {
        return orderedItems.size
    }

    fun setCartItems(newCartItems: MutableList<OrderDetailz>) {
        val newData = ArrayList(newCartItems)
        orderedItems.clear()
        orderedItems.addAll(newData)
        notifyDataSetChanged()
    }
}

class ProductDetailsAdapter(private val productList: List<OrderedItemDetails>,
                            private val onClickListener: (adminId: String) -> Unit) :
    RecyclerView.Adapter<ProductDetailsAdapter.ProductVH>() {

    class ProductVH(val binding: ItemVuOrderSummaryDetailsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        return ProductVH(
            ItemVuOrderSummaryDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
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

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.productName
        holder.binding.tvProductQuantity.text = "Qty: ${product.productQty}"
        holder.binding.tvProductPrice.text = "$${product.productTotalPrice}"

        // Check if there are unread messages for this order
        hasUnreadMessages(product.orderId ?: "") { hasUnread ->
            holder.binding.redDot.visibility = if (hasUnread) View.VISIBLE else View.GONE
        }

        holder.binding.tvChat.setOnClickListener {
            onClickListener.invoke(product.adminId ?: "")
        }

        // Load the product image using Glide
        if (!product.productImgUri.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(product.productImgUri)
                .into(holder.binding.imgProduct)
        } else {
            // If no image URI, you can set a default image or hide the ImageView
            holder.binding.imgProduct.setImageResource(R.drawable.pixabay_misc1)  // Example
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

