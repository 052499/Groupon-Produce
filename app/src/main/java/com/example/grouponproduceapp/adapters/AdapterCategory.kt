package com.example.grouponproduceapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.grouponproduceapp.models.Category
import com.example.grouponproduceapp.databinding.ItemVuProductCategoryBinding


class AdapterCategory(
    val categoryList: ArrayList<Category>,
    val onCategoryClicked: (Category) -> Unit
) : RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>(){
    class CategoryViewHolder(val binding: ItemVuProductCategoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemVuProductCategoryBinding.inflate(LayoutInflater.from(parent.context), parent,false ))
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
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