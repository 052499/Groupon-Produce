package com.app.growceriesadmin.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.growceriesadmin.databinding.ItemVuImgSelectionBinding
import java.lang.reflect.Array

class AdapterSelectedImgs(val selectedImgsUri: ArrayList<Uri>): RecyclerView.Adapter<AdapterSelectedImgs.VH>() {
    class VH(val binding: ItemVuImgSelectionBinding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemVuImgSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun getItemCount(): Int {
        return selectedImgsUri.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val img = selectedImgsUri[position]
        holder.binding.apply {
            rvImageVu.setImageURI(img)
        }

        holder.binding.rvCloseBtn.setOnClickListener {
            if (position < selectedImgsUri.size) {
                selectedImgsUri.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }
}
