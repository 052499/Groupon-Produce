package com.example.grouponproduceapp.userFragments

import CartManager
import PaymentFragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.adapters.AdapterCartItem
import com.example.grouponproduceapp.databinding.FragmentCheckoutBinding
import com.example.grouponproduceapp.models.CartItemWithDetails
import com.example.grouponproduceapp.models.OrderedItemDetails
import com.example.grouponproduceapp.viewmodels.UserVM
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.stripe.android.ApiResultCallback
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {
    private lateinit var binding: FragmentCheckoutBinding
    private lateinit var cartManager: CartManager
    private lateinit var adapterCartItem: AdapterCartItem
    private val viewModel: UserVM by viewModels()
    private lateinit var stripe: Stripe
    private lateinit var sharedPref_cart: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCheckoutBinding.inflate(inflater, container, false)

        sharedPref_cart = requireContext().getSharedPreferences("cart_items", Context.MODE_PRIVATE)
        statusBarColor()

        cartManager =
            CartManager(requireContext().getSharedPreferences("cart_items", Context.MODE_PRIVATE))

        binding.btnBack.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        PaymentConfiguration.init(
            requireContext(),
            "pk_test_51QwuFb4GelsajjQphvyS5zCtymFMK6CcJfUhFIA4uYN4XWmr0we7byTBb7KydjFyNOICynbPquA9Wie9etXxIbib00S68DT9wZ"
        )
        stripe = Stripe(
            requireContext(),
            PaymentConfiguration.getInstance(requireContext()).publishableKey
        )

        setupRecyclerView()
        calculateTotalPrice()

        // Register the FragmentResultListener to listen for payment result
        parentFragmentManager.setFragmentResultListener(
            "paymentResult",
            viewLifecycleOwner
        ) { requestKey, bundle ->
            val paymentMethodCreateParams =
                bundle.getParcelable<PaymentMethodCreateParams>("paymentMethodCreateParams")
            paymentMethodCreateParams?.let {
                handlePayment(it)
            }
        }

        binding.btnProceedToCheckout.setOnClickListener {
            val paymentFragment = PaymentFragment()
            paymentFragment.show(parentFragmentManager, paymentFragment.tag)
        }

        return binding.root
    }

    fun handlePayment(paymentMethodCreateParams: PaymentMethodCreateParams) {
        val stripe = Stripe(
            requireContext(),
            "pk_test_51QwuFb4GelsajjQphvyS5zCtymFMK6CcJfUhFIA4uYN4XWmr0we7byTBb7KydjFyNOICynbPquA9Wie9etXxIbib00S68DT9wZ"
        )
        val clientSecret = "pi_1FzE7A2eZvKYlo2C7XnQ4e6a_secret_RiL6UN5XMnQxzUqSu7lfDkW"
        val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
            paymentMethodCreateParams, clientSecret
        )
        //use stripe.confirmPayment only when you enter publishkey, secretkey and clientsecret from stripe dashboard
//        stripe.confirmPayment(this, confirmParams)

        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch { delay(3000) }
        binding.progressBar.visibility = View.GONE
        Toast.makeText(
            requireContext(),
            "${binding.tvTotalPrice.text} - Payment Successful!",
            Toast.LENGTH_LONG
        ).show()

        saveOrderToFirestore()
    }

    private fun saveOrderToFirestore() {
        Utils.showToast(requireContext(), "Saving products to Firestore")
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val ordersCollection = db.collection("orders")

            // Create a new document reference with an auto-generated ID
            val newOrderRef = ordersCollection.document()

            // Get the auto-generated document ID
            val orderId = newOrderRef.id
            var adminId: String? = ""
            val cartItems = cartManager.getCartItems("cart_items")
//            val orderDetails = mutableListOf<OrderedItemDetails>()
            var totalPrice = 0.0

            val adminOrders = mutableMapOf<String, MutableList<OrderedItemDetails>>()

            lifecycleScope.launch {
                val deferreds = cartItems.map { cartItem ->
                    async {
                        try {
                            val product = viewModel.fetchProductById(cartItem.productId).first()
                            adminId = product.adminUid

                            val cartItemDetails = OrderedItemDetails(
                                adminId = product.adminUid,
                                productId = cartItem.productId,
                                productName = product.productTitle,
                                productQty = cartItem.quantity,
                                productTotalPrice = product.productPrice!!.toInt() * cartItem.quantity,
                                productImgUri = product.productImgsUri?.firstOrNull()
                            )

                            if (adminId != null) {
                                if (adminOrders[adminId] == null) {
                                    adminOrders[adminId!!] = mutableListOf()
                                }
                                adminOrders[adminId]?.add(cartItemDetails)
                            }

//                            orderDetails.add(cartItemDetails)
                            totalPrice += product.productPrice!! * cartItem.quantity
                        } catch (e: Exception) {
                            Log.e("CheckoutFragment", "Error fetching product: ${e.message}")
                        }
                    }
                }

                deferreds.awaitAll()

                adminOrders.forEach { (adminId, items) ->
                    val adminOrderRef = newOrderRef

                    val adminOrderData = mapOf(
                        "orderId" to orderId,
                        "userId" to userId,
                        "adminId" to adminId,
                        "orderPrice" to totalPrice,
                        "orderDetails" to items,
                        "orderStatus" to "processing",
                        "orderDate" to System.currentTimeMillis()
                    )

                    adminOrderRef
                        .set(adminOrderData)
                        .addOnSuccessListener {
                            Log.d(
                                "CheckoutFragment",
                                "Admin order successfully saved to Firestore for admin $adminId."
                            )
                        }
                        .addOnFailureListener { e ->
                            Log.e(
                                "CheckoutFragment",
                                "Error saving admin order to Firestore: ${e.message}"
                            )
                        }

                }
            }

            // Clear cart data after saving order
            sharedPref_cart.edit().clear().apply()
        } else {
            Log.e("CheckoutFragment", "No items in cart to process the order.")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle the Stripe result
        stripe.onPaymentResult(requestCode, data, object : ApiResultCallback<PaymentIntentResult> {
            override fun onSuccess(result: PaymentIntentResult) {
                // Get the payment intent from the result
                val paymentIntent = result.intent
                val status =
                    paymentIntent.status // This is a String, no need for PaymentIntent.Status

                // Handle different payment statuses as strings
                when (status.toString()) {
                    "succeeded" -> {
                        Log.d("StripePayment", "Payment successful!")
                        binding.progressBar.visibility = View.GONE
                        // Update UI or navigate to another screen
                    }

                    "requires_payment_method" -> {
                        Log.e("StripePayment", "Payment failed or user canceled")
                        binding.progressBar.visibility = View.GONE
                        // Show appropriate UI for failure or retry
                    }

                    "canceled" -> {
                        Log.e("StripePayment", "Payment canceled")
                        binding.progressBar.visibility = View.GONE
                        // Handle canceled payments
                    }

                    else -> {
                        Log.d("StripePayment", "Unhandled status: $status")
                        binding.progressBar.visibility = View.GONE
                        // Handle any other status as needed
                    }
                }
            }

            override fun onError(e: Exception) {
                // Handle errors if payment fails
                Log.e("StripePayment", "Error: ${e.message}")
                binding.progressBar.visibility = View.GONE
            }
        })
    }


    private fun setupRecyclerView() {
        // Get cart items from CartManager
//        val cartItems = cartManager.getCartItems("cart_items")
        val allCartItems = cartManager.getCartItems("cart_items")
        val cartItems = allCartItems.filter { it.quantity > 0 }
        Log.d("CHECKOUT-CHECKOUT-CHECKOUT-1", "$allCartItems \n $cartItems")
        val cartItemDetails = mutableListOf<CartItemWithDetails>()
        binding.rvCartItems.layoutManager = LinearLayoutManager(requireContext())

        adapterCartItem = AdapterCartItem(cartItemDetails)  // Set an empty list initially
        binding.rvCartItems.adapter = adapterCartItem  // Attach the adapter to RecyclerView

        lifecycleScope.launch {
            val deferreds = cartItems.map { cartItem ->
                async {
                    try {
                        val product = viewModel.fetchProductById(cartItem.productId).first()
//                        Log.d("CHECKOUT-CHECKOUT-CHECKOUT-2", "$cartItem  price   ${product.productPrice}")
                        val productImageUris = product.productImgsUri
                        val cartItemWithDetails = CartItemWithDetails(
                            productId = cartItem.productId,
                            quantity = cartItem.quantity,
                            productName = product.productTitle, // Fetch product name from product
                            productPrice = product.productPrice!!.toDouble(), // Fetch product price from product
                            productImgUri = productImageUris!!.firstOrNull()
                        )
                        cartItemDetails.add(cartItemWithDetails)
                    } catch (e: Exception) {
                        Log.e("CheckoutFragment", "Error fetching product: ${e.message}")
                    }
                }
            }

            deferreds.awaitAll()
            if (cartItemDetails.isNotEmpty()) {
                adapterCartItem.setCartItems(cartItemDetails)
            } else {
                Log.e("tag", "No products found for cart.")
            }
        }
    }

    private fun calculateTotalPrice() {
        val allCartItems = cartManager.getCartItems("cart_items")
        val cartItems = allCartItems.filter { it.quantity > 0 }
        var totalPrice = 0.0
        Log.d("CHECKOUT-CalculatePrice-1", "I am called to calculate Total price  $cartItems")

        lifecycleScope.launch {
            for (item in cartItems) {
                Log.d("CHECKOUT-CalculatePrice-1", "the items are:    $item")
                try {
                    val productPrice = getProductPrice(item.productId)
                    totalPrice += productPrice * item.quantity
                    Log.d("CHECKOUT-CalculatePrice-2", "$item  $totalPrice")
                } catch (e: Exception) {
                    Log.e("CheckoutFragment-2", "Error fetching product price: ${e.message}")
                }
            }
            Log.d("CHECKOUT-CalculatePrice-3", "$cartItems    $totalPrice")
            try {
                binding.tvTotalPrice.text = "Total Price: $${"%.2f".format(totalPrice)}"
            } catch (e: Exception) {
                Log.d("CHECKOUT-CalculatePrice-4", "$totalPrice  but not successful")
            }
        }
    }

    private fun onQuantityUpdated(cartItem: CartItemWithDetails) {
        cartManager.addProductToCart(cartItem.productId, cartItem.quantity)
        calculateTotalPrice()
    }

    private suspend fun getProductPrice(productId: String): Double {
        return try {
            val product = viewModel.fetchProductById(productId).first()
            product.productPrice?.toDouble() ?: 0.0
        } catch (e: Exception) {
            Log.e("CheckoutFragment", "Error fetching product price: ${e.message}")
            0.0
        }
    }

    private fun statusBarColor() {
        activity?.window?.apply {
            val statusBarColors =
                ContextCompat.getColor(requireContext(), R.color.defaultBackground)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }


}



