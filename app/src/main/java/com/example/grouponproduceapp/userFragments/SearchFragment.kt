package com.example.grouponproduceapp.userFragments

import CartManager
import android.content.Context
import android.content.SharedPreferences
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
import com.example.grouponproduceapp.CartListener
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.adapters.AdapterProduct
import com.example.grouponproduceapp.models.Product
import com.example.grouponproduceapp.viewmodels.UserVM
import com.example.grouponproduceapp.databinding.FragmentSearchBinding
import com.example.grouponproduceapp.databinding.ItemVuProductBinding
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding
    private lateinit var adapterProduct : AdapterProduct
    val userVM: UserVM by viewModels()
    private lateinit var cartManager: CartManager
    private lateinit var sharedPref : SharedPreferences
    private var cartListener : CartListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        sharedPref = requireContext().getSharedPreferences("cart_items", Context.MODE_PRIVATE)
        cartManager = CartManager(sharedPref)

        statusBarColor()
        getAllProducts()
        searchProducts()

        binding.btnBack.setOnClickListener{
            activity?.supportFragmentManager?.popBackStack()
        }

//        binding.cvSearch.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
//            // Navigate back to HomeFragment
//            activity?.supportFragmentManager?.popBackStack()
//        }

        return binding.root
    }

    private fun getAllProducts() {
        lifecycleScope.launch {
            userVM.fetchProducts().collect(){products ->
                if (products.isEmpty()){
                    binding.rvProducts.visibility = View.GONE
                    binding.tvNoProduct.visibility = View.VISIBLE
                }
                else {
                    binding.rvProducts.visibility = View.VISIBLE
                    binding.tvNoProduct.visibility = View.GONE
                }
                adapterProduct = AdapterProduct(
                    ::onAddBtnClicked,
                    ::onIncrementBtnClicked,
                    ::onDecrementBtnClicked,
                    sharedPref
                )
                binding.rvProducts.adapter = adapterProduct
                adapterProduct.differList.submitList(products)
                adapterProduct.originalList = products as ArrayList<Product>
            }
        }
    }

    private fun searchProducts(){
        adapterProduct = AdapterProduct(
            ::onAddBtnClicked,
            ::onIncrementBtnClicked,
            ::onDecrementBtnClicked,
            sharedPref
        )
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val query = p0.toString().trim()
                adapterProduct.filter.filter(query)
            }
            override fun afterTextChanged(p0: Editable?) { }
        })
    }

    private fun onAddBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        Log.d("add-add-add-add-add1", productBinding.tvProductCount.text.toString().toInt().toString())

        productBinding.tvAdd.visibility = View.GONE
        productBinding.llProductCount.visibility = View.VISIBLE


        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        Log.d("add-add-add-add-add2", itemCount.toString())
        productBinding.tvProductCount.text= itemCount.toString()

        cartManager.addProductToCart(product.productId, 1)
//        productBinding.tvProductCount.text = itemCount.toString()

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemsCount(1)
    }

    fun onIncrementBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        Log.d("inc-inc-inc-inc-inc1", productBinding.tvProductCount.text.toString().toInt().toString())

        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount++
        Log.d("inc-inc-inc-inc-inc2", itemCount.toString())

        productBinding.tvProductCount.text= itemCount.toString()
        //new1
        cartManager.addProductToCart(product.productId, 1)

        cartListener?.showCartLayout(1)
        cartListener?.savingCartItemsCount(1)
    }
    fun onDecrementBtnClicked(product: Product, productBinding: ItemVuProductBinding){
        Log.d("dec-dec-dec-dec-dec1", productBinding.tvProductCount.text.toString().toInt().toString())
        var itemCount = productBinding.tvProductCount.text.toString().toInt()
        itemCount--
        Log.d("dec-dec-dec-dec-dec2", itemCount.toString())
        if (itemCount > 0) {
            cartManager.addProductToCart(product.productId, -1)
        } else if (itemCount == 0) {
            Log.d("dec-dec-dec-dec-dec3", itemCount.toString())

            cartManager.addProductToCart(product.productId, -1)
            productBinding.tvAdd.visibility = View.VISIBLE
            productBinding.llProductCount.visibility = View.GONE
        }

        //new1
//        cartManager.addProductToCart(product.productId, -1)
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

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.defaultBackground)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}
