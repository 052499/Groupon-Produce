package com.example.grouponproduceapp.adminFragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.grouponproduceapp.Constants
import com.example.grouponproduceapp.adapters.AdapterCategoryAdmin
import com.example.grouponproduceapp.adapters.AdapterProductAdmin
import com.example.grouponproduceapp.models.Category
import com.example.grouponproduceapp.models.Product
import com.example.grouponproduceapp.viewmodels.AdminVM
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.activity.AuthMainActivity
import com.example.grouponproduceapp.databinding.FragmentHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeAdminFragment : Fragment() {

    lateinit var binding: FragmentHomeAdminBinding
    lateinit var adapterProduct: AdapterProductAdmin
    val adminVM: AdminVM by viewModels()

    override fun onResume() {
        super.onResume()
        getAllProducts("All")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeAdminBinding.inflate(layoutInflater)
        adapterProduct = AdapterProductAdmin(adminVM)
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
            adminVM.fetchProducts(FirebaseAuth.getInstance().currentUser?.uid, categoryTitle).collect(){
                Log.d("HAF-HAF-HAF-HAF", "$categoryTitle        ${it}")
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvNoProduct.visibility = View.GONE
                }
//                val adapterProduct = AdapterProduct(::onSaveBtnClicked)
//                adapterProduct = AdapterProductAdmin(adminVM)
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