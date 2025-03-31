package com.example.grouponproduceapp.userFragments

import CartManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grouponproduceapp.CartListener
import com.example.grouponproduceapp.adapters.AdapterProduct
import com.example.grouponproduceapp.models.Product
//import com.app.growceries.roomdb.CartItemsEntity
import com.example.grouponproduceapp.viewmodels.UserVM
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentCategoryBinding
import com.example.grouponproduceapp.databinding.ItemVuProductBinding
import kotlinx.coroutines.launch


class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private val viewModel : UserVM by viewModels()
    private lateinit var adapterProduct: AdapterProduct
    private lateinit var sharedPref: SharedPreferences
    private lateinit var cartManager: CartManager

    private var categoryTitle : String? = null
    private var cartListener: CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        sharedPref = requireContext().getSharedPreferences("cart_items", Context.MODE_PRIVATE)
        cartManager = CartManager(sharedPref)


        getCategoryProducts()
        fetchCategoryProducts()
        onSearchMenuClick()
        onNavigationIconClick()

        return binding.root
    }

    private fun onNavigationIconClick() {
        binding.toolBarCategoryFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun onSearchMenuClick() {
        binding.toolBarCategoryFragment.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.searchMenu ->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true
                }
                else -> {false}
            }
        }
    }

    private fun fetchCategoryProducts() {
        lifecycleScope.launch {
            viewModel.getCategoryProducts(categoryTitle!!).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvNoProduct.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(::onAddBtnClicked, ::onIncrementBtnClicked, ::onDecrementBtnClicked, sharedPref)
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differList.submitList(it)
            }
        }
    }

    private fun getCategoryProducts() {
        val bundle = arguments
        categoryTitle = bundle?.getString("category")
        binding.tvTitleCategory.text = categoryTitle
    }

    private fun onAddBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE

        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text= itemCount.toString()

        cartManager.addProductToCart(product.productId, 1)
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemsCount(1)
    }

    fun onIncrementBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        productBinding.tvProductCount.text= itemCount.toString()

        cartManager.addProductToCart(product.productId, 1)

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemsCount(1)
    }
    fun onDecrementBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount--
        if (itemCount < 1) {
            itemCount = 0
            cartManager.removeProductFromCart(product.productId)
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
        }

        cartManager.addProductToCart(product.productId, -1)
        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(-1)
        cartListener?.savingCartItemsCount(-1)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(context is CartListener ) {
            cartListener = context
        }
        else {
            throw ClassCastException("Please implement CartListener")
        }
    }
}