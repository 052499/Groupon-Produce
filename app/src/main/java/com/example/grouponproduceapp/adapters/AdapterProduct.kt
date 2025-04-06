package com.example.grouponproduceapp.adapters

import CartManager
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.grouponproduceapp.FilteringProducts
import com.example.grouponproduceapp.databinding.ItemVuProductBinding
import com.example.grouponproduceapp.models.Product

class AdapterProduct(
    val onAddBtnClicked: (Product, ItemVuProductBinding) -> Unit,
    val onIncrementBtnClicked: (Product, ItemVuProductBinding) -> Unit,
    val onDecrementBtnClicked: (Product, ItemVuProductBinding) -> Unit,
    sharedPref: SharedPreferences
) : RecyclerView.Adapter<AdapterProduct.VH>(), Filterable  {
    class VH(val binding: ItemVuProductBinding): ViewHolder(binding.root)

    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differList = AsyncListDiffer(this, diffUtil)
    private val cartManager = CartManager(sharedPref) // Instantiate CartManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemVuProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differList.currentList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = differList.currentList[position]

        holder.binding.apply {
            val imgList = ArrayList<SlideModel>()
            val productImg = product.productImgsUri

            for (i in 0 until productImg?.size!!){
                imgList.add(SlideModel(product.productImgsUri!![i].toString()))
            }
            imageSlider.setImageList((imgList))

            tvProductTitle.text= product.productTitle
            val qty= product.productQty.toString()+ product.productUnit
            tvProductQty.text = qty
            val productQuantity= cartManager.getProductQuantity(product.productId).toString().toInt()
//            Log.d(":::::::::::::::::::init", productQuantity.toString())
            tvProductCount.text = productQuantity.toString()
            tvProductPrice.text = "$${product.productPrice}"

            if (productQuantity > 0) {
                tvAdd.visibility = View.GONE
                llProductCount.visibility = View.VISIBLE
                tvProductCount.text = productQuantity.toString()  // Set the count to the actual count in the cart
            } else {
                tvAdd.visibility = View.VISIBLE
                llProductCount.visibility = View.GONE
            }

            tvAdd.setOnClickListener {
//                Log.d(":::::::::::::::::::add","add clicked")
                onAddBtnClicked(product, this)
            }
            tvIncrement.setOnClickListener{
//                Log.d(":::::::::::::::::::+","increment clicked")
                onIncrementBtnClicked(product, this)
            }
            tvDecrement.setOnClickListener {
//                Log.d(":::::::::::::::::::-","decrement clicked")
                onDecrementBtnClicked(product, this)
            }
        }
    }

    val filtered: FilteringProducts? = null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filtered == null) return FilteringProducts(this, originalList)
        return filtered
    }
}