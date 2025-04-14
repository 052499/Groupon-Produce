package com.example.grouponproduceapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.databinding.ItemVuCartBinding
import com.example.grouponproduceapp.models.CartItemWithDetails
import com.bumptech.glide.Glide

class AdapterCartItem(private val cartItems: MutableList<CartItemWithDetails>,
                      private val onQuantityChanged: (CartItemWithDetails, Int) -> Unit,
                      private  val onPriceUpdated: () -> Unit)
    : RecyclerView.Adapter<AdapterCartItem.VH>() {

    class VH(val binding: ItemVuCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemVuCartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cartItem = cartItems[position]

        // Display product details
        holder.binding.tvProductName.text = cartItem.productName // Fetch actual name from product data
        holder.binding.tvProductQuantity.text = cartItem.quantity.toString()
        holder.binding.tvProductPrice.text = cartItem.productPrice.toString()
        cartItem.productImgUri?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .into(holder.binding.imgProduct)
        }

        // Set up the decrement button functionality
        holder.binding.btnDecrement.setOnClickListener {
            if (cartItem.quantity >= 1) {  // Prevent quantity from going below 1
                cartItem.quantity--
                holder.binding.tvProductQuantity.text = cartItem.quantity.toString()
                // Check if quantity is now zero
                if (cartItem.quantity == 0) {
                    // Remove the item from the list if quantity is zero
                    try{
                        cartItems.removeAt(position)
                    } catch (e: Exception){
                        Log.d("Exception Error", e.toString())
                    }
                    onQuantityChanged(cartItem, -1)
                    notifyItemRemoved(position)
                    notifyDataSetChanged()
                    onPriceUpdated()
//                    Log.d("Cart", "Item removed at position: $position")
                } else {
                    // Otherwise update the quantity
                    onQuantityChanged(cartItem, -1)
                    onPriceUpdated()
                }
            }
        }

        // Set up the increment button functionality
        holder.binding.btnIncrement.setOnClickListener {
            val stock = cartItem.productStock ?: 0
            if (cartItem.quantity < stock){
                cartItem.quantity++
                holder.binding.tvProductQuantity.text = cartItem.quantity.toString()
                onQuantityChanged(cartItem, 1)
                onPriceUpdated()
            } else {
                onQuantityChanged(cartItem, 0)
            }

        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    fun setCartItems(newCartItems: List<CartItemWithDetails>) {
        val newData = ArrayList(newCartItems)
        cartItems.clear()
        cartItems.addAll(newData)
        notifyDataSetChanged()
    }
}

