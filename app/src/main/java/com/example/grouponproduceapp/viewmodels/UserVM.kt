package com.example.grouponproduceapp.viewmodels

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.grouponproduceapp.models.CartItem
import com.example.grouponproduceapp.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.lang.reflect.Type

class UserVM(application: Application) : AndroidViewModel(application) {

    val sharedPreferences: SharedPreferences = application.getSharedPreferences("cart_items", MODE_PRIVATE)

    fun fetchProductById(productId: String): Flow<Product> = callbackFlow {
        val dbPath = FirebaseDatabase.getInstance().getReference("Admins").child("products").child(productId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(Product::class.java)
                product?.let { trySend(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException()) // Close flow on cancellation
            }
        }
        dbPath.addValueEventListener(eventListener)
        awaitClose { dbPath.removeEventListener(eventListener) }
    }


    fun fetchProducts(): Flow<List<Product>> = callbackFlow {
        val dbPath = FirebaseDatabase.getInstance().getReference("Admins").child("products")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbPath.addValueEventListener(eventListener)
        awaitClose { dbPath.removeEventListener(eventListener) }
    }

    fun getCategoryProducts(category: String): Flow<List<Product>> = callbackFlow {
        val getInstance = FirebaseDatabase.getInstance()
        val dbPath = getInstance.getReference("Admins").child("products")


        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val product = product.getValue(Product::class.java)
                    if (product != null) {
                        if (product.productCategory == category) {
                            products.add(product)
                        }
                    }
                }
                trySend(products)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("fetchProductsByCategory", "Error fetching products: ${error.message}")
            }
        }
        dbPath.addValueEventListener(eventListener)
        awaitClose { dbPath.removeEventListener(eventListener) }
    }

    fun savingCartItemsTotalCount(itemCount: Int) {
        sharedPreferences.edit().putInt("itemCount", itemCount).apply()
    }

    fun fetchTotalItemCount() : MutableLiveData<Int> {
        val totalItemCountLiveData = MutableLiveData<Int>()
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        var cartItems= Gson().fromJson(cartJson, type) as List<CartItem>

        var totalItemCount = 0

        cartItems.forEach { cartItem ->
//            Log.d("ooooooooooo", cartItem.quantity.toString())
            totalItemCount += cartItem.quantity
        }
        totalItemCountLiveData.value = totalItemCount
        return totalItemCountLiveData
    }

    fun saveUserType(context: Context, userType: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_TYPE", userType)
        editor.apply() // Asynchronously saves the data
    }

    fun getUserType(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_TYPE", null) // Default is null if not found
    }

}