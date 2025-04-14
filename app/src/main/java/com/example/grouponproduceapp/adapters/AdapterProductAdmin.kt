package com.example.grouponproduceapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grouponproduceapp.FilteringProductsAdmin
import com.example.grouponproduceapp.models.Product
import com.denzcoskun.imageslider.models.SlideModel
import com.example.grouponproduceapp.databinding.ItemVuProductAdminBinding
import com.example.grouponproduceapp.viewmodels.AdminVM

class AdapterProductAdmin(val adminVM: AdminVM) : RecyclerView.Adapter<AdapterProductAdmin.VH>(), Filterable {

    class VH(val binding: ItemVuProductAdminBinding): ViewHolder(binding.root)
    val diffUtil = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differList = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemVuProductAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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
            tvProductQty.text= qty
            tvProductStock.text= product.productStock.toString()
            tvProductPrice.text = "$${product.productPrice}"

            tvRemoveItem.setOnClickListener {
                adminVM.removeProductFromFirestore(product.productId) { isSuccess ->
                    if (isSuccess) {
//                        Log.d("APA-APA-APA-APA-1", "$isSuccess")
                        val updatedList = differList.currentList.toMutableList()
//                        Log.d("APA-APA-APA-APA-2", updatedList.toString())

                        if (position >= 0 && position < updatedList.size) {
                            Log.d(
                                "removeProduct",
                                "List size: ${updatedList.size}, Position: $position"
                            )

                            updatedList.removeAt(position)
                            differList.submitList(updatedList) // Submit the updated list
                            notifyItemRemoved(position) // Notify the adapter that the item has been removed
                        } else {
                            Log.d(
                                "removeProduct",
                                "Invalid position: $position, or the list is empty"
                            )
                        }
                    }
                    else {
                        Log.d("------- tag removeProduct", "Error in product deletion")
                    }
                }
            }
        }
    }

    val filtered: FilteringProductsAdmin? = null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
        if (filtered == null) return FilteringProductsAdmin(this, originalList)
        return filtered
    }


}
