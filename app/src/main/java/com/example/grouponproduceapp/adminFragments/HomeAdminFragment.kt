package com.app.growceries.adminFragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.app.growceries.Constants
import com.app.growceries.adapters.AdapterCategoryAdmin
import com.app.growceries.adapters.AdapterProductAdmin
import com.app.growceries.models.Category
import com.app.growceries.models.Product
import com.app.growceries.viewmodels.AdminVM
import com.app.growceries.R
import com.app.growceries.Utils
import com.app.growceries.activity.AuthMainActivity
import com.app.growceries.databinding.FragmentHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeAdminFragment : Fragment() {

    lateinit var binding: FragmentHomeAdminBinding
    lateinit var adapterProduct: AdapterProductAdmin
    val adminVM: AdminVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(layoutInflater)
//        statusBarColor()

        val logoutButton = binding.btnLogout
        logoutButton.setOnClickListener {
            logout()
        }

        setCategories()
        searchProducts()
        getAllProducts("All")

        return binding.root
    }

    private fun searchProducts(){
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString().trim()
                adapterProduct.filter?.filter(query)
            }

            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun getAllProducts(categoryTitle: String?) {
        lifecycleScope.launch {
            adminVM.fetchProducts(categoryTitle).collect(){
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvNoProduct.visibility = View.GONE
                }
//                val adapterProduct = AdapterProduct(::onSaveBtnClicked)
                adapterProduct = AdapterProductAdmin()
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differList.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
            }
        }
    }

    private fun logout() {
        val auth= FirebaseAuth.getInstance()
        val userToLogout = auth.currentUser?.email
        auth.signOut()
        val intent = Intent(requireActivity(), AuthMainActivity::class.java)
        Utils.showToast(requireContext(), "${userToLogout} is logged out")
        startActivity(intent)
    }


    private fun setCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0 until Constants.allCategoriesIcons.size){
            categoryList.add(Category(Constants.allCategories[i], Constants.allCategoriesIcons[i]))
        }
        binding.rvCategories.adapter = AdapterCategoryAdmin(categoryList, ::onCategoryClicked)
    }

    private fun onCategoryClicked(category: Category){
        getAllProducts(category.title)
    }

//    fun onSaveBtnClicked(product: Product){
//        val editProduct = EditProductLayoutBinding.inflate(LayoutInflater.from(requireContext()))
//        editProduct.apply {
//            etProductQty.setText(product.productQty.toString())
//            etProductType.setText(product.productType)
//            etProductPrice.setText(product.productPrice.toString())
//            etProductStock.setText(product.productStock.toString())
//            etProductUnit.setText(product.productUnit)
//            etProductCategory.setText(product.productCategory)
//            etProductTitle.setText(product.productTitle)
//        }
//        val alertDialog = AlertDialog.Builder(requireContext())
//            .setView(editProduct.root)
//            .create()
//        alertDialog.show()
//    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(),
                R.color.blue
            )
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}