import android.content.SharedPreferences
import android.util.Log
import com.example.grouponproduceapp.models.CartItem
import com.example.grouponproduceapp.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CartManager(private val sharedPreferences: SharedPreferences) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    // Function to add a product to the cart or update its quantity
    fun addProductToCart(productId: String, quantity: Int) {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type =
            object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)

        // Check if the product is already in the cart
        val existingItem = cartItems.find { it.productId == productId }
        val productRef = FirebaseDatabase.getInstance().getReference("Admins/products").child(productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val product = snapshot.getValue(Product::class.java)
                    val stock = product?.productStock ?: 0
                    if (quantity == 1) {
                        if (existingItem == null || existingItem.quantity < stock) {
                            if (existingItem != null) {
                                existingItem.quantity += 1
                                Log.d("APTC-APTC-APTC-APTC-APTC+1", "item found: quantity: $quantity  stock: $stock  existingItemQty: ${existingItem?.quantity}  ${productId}")
                            } else {
                                cartItems.add(CartItem(productId, 1))
                                Log.d("APTC-APTC-APTC-APTC-APTC+1", "item added: quantity: $quantity  stock: $stock  existingItemQty: ${existingItem?.quantity}   ${productId}")
                            }
                            val updatedCartJson = Gson().toJson(cartItems)
                            editor.putString("cart_items", updatedCartJson).apply()
                            Log.d("APTC-APTC-APTC-APTC-APTC+1", "all items: ${updatedCartJson}")

                        } else {
                            Log.d("CartManager", "Cannot add more items. Stock limit reached.")
                        }
                    }
                    else if (quantity == -1) {
                        Log.d("APTC-APTC-APTC-APTC-APTC-1", quantity.toString())
                        if (existingItem != null && existingItem.quantity > 0) {
                            existingItem.quantity -= 1
                            Log.d("APTC-APTC-APTC-APTC-APTC-11", "${quantity}     ${existingItem.quantity}")
                            if (existingItem.quantity == 0) {
                                Log.d("APTC-APTC-APTC-APTC-APTC-12", existingItem.toString())
                                removeProductFromCart(productId) // Remove product if quantity is zero
                            }
                            val updatedCartJson = Gson().toJson(cartItems)
                            editor.putString("cart_items", updatedCartJson).apply()
                        }
                    }

                } else {
                    // If the product doesn't exist in the database
                    Log.d("RealtimeDB", "Product document does not exist.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("RealtimeDB", "Error fetching product: ${error.message}")
            }
        })
    }

    // Function to get all items from the cart
    fun getCartItems(sharedPref: String): List<CartItem> {
        val cartJson = sharedPreferences.getString(sharedPref, "[]")
        val type: Type =
            object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        return Gson().fromJson(cartJson, type)
    }

    // Function to get quantity of a product in cart
    fun getProductQuantity(productId: String): Int {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type =
            object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)
        val cartItem = cartItems.find { it.productId == productId }
        Log.d("GPQ-GPQ-GPQ-GPQ-GPQ", cartItem?.quantity.toString())
        return cartItem?.quantity ?: 0 // Return 0 if not found
    }

    fun getTotalItemCount(): Int {
        val cartItems = getCartItems("cart_items")
        return cartItems.sumOf { it.quantity.takeIf { it > 0 } ?: 0 }
    }

    // Function to remove an item from the cart
    fun removeProductFromCart(productId: String) {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type =
            object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)

        // Remove the product with the given productId
        val updatedCartItems = cartItems.filterNot { it.productId == productId }
        // Save the updated list back to SharedPreferences
        val updatedCartJson = Gson().toJson(updatedCartItems)
        Log.d("RPFT-RPFT-RPFT-RPFT-RPFT-1", updatedCartJson.toString())

        try {
            editor.putString("cart_items", updatedCartJson).commit()
            val savedCartJson = sharedPreferences.getString("cart_items", "[]")
            Log.d("RPFT-RPFT-RPFT-RPFT-RPFT-2", "Saved Cart JSON: $savedCartJson")
        } catch (e: Exception) {
            Log.e("RPFT-RPFT-RPFT-RPFT-RPFT-2", "Error saving cart items", e)
        }


    }
}


