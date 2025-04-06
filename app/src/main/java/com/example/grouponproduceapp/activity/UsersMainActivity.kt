package com.example.grouponproduceapp.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.grouponproduceapp.CartListener
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.viewmodels.UserVM
import com.example.grouponproduceapp.databinding.ActivityUsersBinding
import com.example.grouponproduceapp.models.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class UsersMainActivity : AppCompatActivity(), CartListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel : UserVM by viewModels()
    private lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = this.getSharedPreferences("cart_items", Context.MODE_PRIVATE)

        updateCartVisibility()
        binding.llCheckout.setOnClickListener {
            val navController = findNavController(R.id.fragmentContainerView2)
            navController.navigate(R.id.action_global_checkoutFragment)
            binding.llCart.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        updateCartVisibility()
    }

    fun hideCheckoutButton() {
        binding.llCart.visibility = View.GONE
    }

    fun updateCartVisibility() {
        val totalItemCount = viewModel.fetchTotalItemCount().value ?: 0
//        Log.d("UpdateCartVisibility","total item count is    $totalItemCount")
        if (totalItemCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.nInCart.text = totalItemCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        updateCartVisibility()
    }

    override fun showCartLayout(itemCount: Int) {
        var cartJson= sharedPref.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)
        var existingItems = cartItems.filter { it.quantity > 0 }
        var prevCount = 0
        existingItems.forEach {
            prevCount += it.quantity
        }

        val updatedTotalCount = prevCount + itemCount
        if(updatedTotalCount > 0) {
            binding.llCart.visibility = View.VISIBLE
            binding.nInCart.text = updatedTotalCount.toString()
        } else {
            binding.llCart.visibility = View.GONE
            binding.nInCart.text = "0"
        }
    }

    override fun savingCartItemsCount(itemCount: Int) {
        viewModel.fetchTotalItemCount().observe(this){
            viewModel.savingCartItemsTotalCount(it+itemCount)
        }
    }
}