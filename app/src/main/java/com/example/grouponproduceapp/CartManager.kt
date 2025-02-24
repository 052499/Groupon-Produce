import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.grouponproduceapp.models.CartItem
import com.example.grouponproduceapp.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CartManager(private val sharedPreferences: SharedPreferences) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    // Function to add a product to the cart or update its quantity
    fun addProductToCart(productId: String, quantity: Int) {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems : ArrayList<CartItem> = Gson().fromJson(cartJson, type)

        // Check if the product is already in the cart
        val existingItem = cartItems.find { it.productId == productId }

        if (existingItem != null) {
            // If it exists, update the quantity
            if (existingItem.quantity + quantity >= 0) {
                existingItem.quantity += quantity
            } else {
                existingItem.quantity = 0 // Set quantity to 0 if the result is negative
            }
        } else if (existingItem == null && quantity!= -1) {
            // If it's new, add a new cart item
            cartItems.add(CartItem(productId, quantity))
        }
        // Save the updated cart back to SharedPreferences
        val updatedCartJson = Gson().toJson(cartItems)
        editor.putString("cart_items", updatedCartJson).apply()
    }

    // Function to get all items from the cart
    fun getCartItems(): List<CartItem> {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        return Gson().fromJson(cartJson, type)
    }

    // Function to get quantity of a product in cart
    fun getProductQuantity(productId: String): Int {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)
        val cartItem = cartItems.find { it.productId == productId }
        return cartItem?.quantity ?: 0 // Return 0 if not found
    }

    fun getTotalItemCount(): Int {
        val cartItems = getCartItems()
        return cartItems.sumOf { it.quantity.takeIf { it > 0 } ?: 0 }
    }

    // Function to remove an item from the cart
    fun removeProductFromCart(productId: String) {
        val cartJson = sharedPreferences.getString("cart_items", "[]")
        val type: Type = object : TypeToken<ArrayList<CartItem>>() {}.type // Specify the correct type
        val cartItems: ArrayList<CartItem> = Gson().fromJson(cartJson, type)

        // Remove the product with the given productId
        val updatedCartItems = cartItems.filterNot { it.productId == productId }

        // Save the updated list back to SharedPreferences
        val updatedCartJson = Gson().toJson(updatedCartItems)
        editor.putString("cart_items", updatedCartJson).apply()
    }
}


