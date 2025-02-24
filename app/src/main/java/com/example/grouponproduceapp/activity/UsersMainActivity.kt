package com.app.growceries.activity

import CartManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.app.growceries.CartListener
import com.app.growceries.R
import com.app.growceries.viewmodels.UserVM
import com.app.growceries.databinding.ActivityUsersBinding
import com.app.growceries.userFragments.UserChatFragment

class UsersMainActivity : AppCompatActivity(), CartListener {
    private lateinit var binding: ActivityUsersBinding
    private val viewModel : UserVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateCartVisibility()
        binding.llCheckout.setOnClickListener {
            val navController = findNavController(R.id.fragmentContainerView2)
            navController.navigate(R.id.action_global_checkoutFragment)
            binding.llCart.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        // Every time the activity comes to the foreground, update the cart count
        updateCartVisibility()
    }

    private fun updateCartVisibility() {
        val totalItemCount = viewModel.fetchTotalItemCount().value ?: 0
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

//    private fun getCartItemsCount() {
//        viewModel.fetchTotalItemCount().observe(this){
//            if(it>0){
//                binding.llCart.visibility = View.VISIBLE
//                binding.nInCart.text = it.toString()
//            } else {
//                binding.llCartItems.visibility = View.GONE
//            }
//        }
//    }

    override fun showCartLayout(itemCount: Int) {
        val prevCount = binding.nInCart.text.toString().toInt()
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