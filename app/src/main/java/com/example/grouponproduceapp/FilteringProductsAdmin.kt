package com.example.grouponproduceapp

import com.example.grouponproduceapp.adapters.AdapterProductAdmin
import com.example.grouponproduceapp.models.Product
import java.util.Locale

class FilteringProductsAdmin(
    val adapter: AdapterProductAdmin,
    val filter: ArrayList<Product>
): android.widget.Filter() {
    override fun performFiltering(p0: CharSequence?): FilterResults {
        var result = FilterResults()

        if (!p0.isNullOrEmpty()) {
            val filteredList = ArrayList<Product>()
            val query = p0.toString().trim().lowercase().split(" ")

            for (product in filter){
//                if(query.any{product.productTitle?.lowercase(Locale.getDefault())!!.contains(it) || product.productCategory!!.lowercase(Locale.getDefault()).contains(it) || product.productType!!.lowercase(Locale.getDefault()).contains(it)
                if(query.any{product.productTitle?.lowercase(Locale.getDefault())!!.contains(it) || product.productCategory!!.lowercase(Locale.getDefault()).contains(it)
                    }) {
                    filteredList.add(product)
                }
            }
            result.values = filteredList
            result.count = filteredList.size
        } else {
            result.values = filter
            result.count = filter.size
        }

        return result
    }

    override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
        adapter.differList.submitList(p1?.values as ArrayList<Product>)
    }
}