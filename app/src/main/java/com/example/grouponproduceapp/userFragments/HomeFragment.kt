package com.example.grouponproduceapp.userFragments

import CartManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.grouponproduceapp.Constants
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.activity.AuthMainActivity
import com.example.grouponproduceapp.adapters.AdapterCategory
import com.example.grouponproduceapp.models.Category
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var cartManager: CartManager
    private lateinit var sharedPref : SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        sharedPref = requireContext().getSharedPreferences("cart_items", Context.MODE_PRIVATE)
        cartManager = CartManager(sharedPref)

        // Initialize the dropdown button or image view
        val ivDropdownMenu: ImageView = binding.ivDropdownMenu
        ivDropdownMenu.setOnClickListener {
            // Create and show the PopupMenu
            val popupMenu = PopupMenu(requireContext(), ivDropdownMenu)
            val menuInflater = popupMenu.menuInflater
            menuInflater.inflate(R.menu.menu_options, popupMenu.menu)
            // Set listener for menu item clicks
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menuProfile -> navigateToProfile()
                    R.id.orderStatus -> navigateToOrderStatus()
                    R.id.menuLogout -> logout()
                    else -> false
                }
                true
            }
            popupMenu.show()
        }

        statusBarColor()
        setAllCategories()
        navigateToSearchFragment()
//        startChat()

        return binding.root
    }

    private fun navigateToOrderStatus() {
        findNavController().navigate(R.id.action_homeFragment_to_orderSummaryFragment)
//        val targetFragment= OrderSummaryFragment()
//        parentFragmentManager.beginTransaction().replace(R.id.homeFragment, targetFragment)
//            .addToBackStack(null)
//            .commit()
    }

    private fun navigateToProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

//    private fun startChat() {
//        binding.fabChat.setOnClickListener {
//            val userChatFragment = UserChatFragment()
//            userChatFragment.show(requireActivity().supportFragmentManager, "ChatDialog")
//        }
//    }

    private fun onCategoryClicked(category: Category) {
        val bundle = Bundle()
        bundle.putString("category", category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, bundle)
    }

    private fun navigateToSearchFragment() {
        binding.cvSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun logout() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()

        val auth = FirebaseAuth.getInstance()
        val userToLogout = auth.currentUser?.email
        auth.signOut()
        val intent = Intent(requireActivity(), AuthMainActivity::class.java)
        Utils.showToast(requireContext(), "${userToLogout} is logged out")
        startActivity(intent)
    }

    private fun setAllCategories() {
        val categoryList = ArrayList<Category>()

        for (i in 0..Constants.allCategories.size - 1) {
            if (Constants.allCategories[i] == "All") {
            } else {
                categoryList.add(
                    Category(
                        Constants.allCategories[i],
                        Constants.allCategoriesIcons[i]
                    )
                )
            }
        }
        binding.rvCategories.adapter = AdapterCategory(categoryList, ::onCategoryClicked)
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(
                requireContext(),
                R.color.blue )
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}