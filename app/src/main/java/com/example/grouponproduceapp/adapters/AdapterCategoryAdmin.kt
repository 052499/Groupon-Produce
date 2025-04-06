package com.example.grouponproduceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grouponproduceapp.models.Category
import com.example.grouponproduceapp.databinding.ItemVuProductCategoryBinding


class AdapterCategoryAdmin(
    val categoryList: ArrayList<Category>,
    val onCategoryClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategoryAdmin.CatVH>() {
    class CatVH(val binding: ItemVuProductCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatVH {
        return CatVH(ItemVuProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CatVH, position: Int) {
        val category = categoryList[position]
        holder.binding.apply {
            ivImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }

        holder.itemView.setOnClickListener{
            onCategoryClicked(category)
        }
    }
}