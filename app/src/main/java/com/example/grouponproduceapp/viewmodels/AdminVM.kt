package com.example.grouponproduceapp.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.grouponproduceapp.models.OrderDetailz
import com.example.grouponproduceapp.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class AdminVM: ViewModel() {

    private val _isImgsUploaded = MutableStateFlow(false)
    var isImgsUploaded: StateFlow<Boolean> = _isImgsUploaded

    private val _downloadedUrls = MutableStateFlow<ArrayList<String?>>(arrayListOf())
    var downloadedUrls : StateFlow<ArrayList<String?>> = _downloadedUrls

    private val _isProductSaved = MutableStateFlow<Boolean>(false)
    var isProductSaved : StateFlow<Boolean> = _isProductSaved


    fun saveImgsInDB(imgUri: ArrayList<Uri>){
        val downloadUrls = ArrayList<String?>()

        imgUri.forEach {uri ->
            val imgRef = FirebaseStorage.getInstance().reference.child("images").child(UUID.randomUUID().toString())
            imgRef.putFile(uri).continueWithTask {task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    } }
                imgRef.downloadUrl
            }.addOnCompleteListener {task ->
                    val url = task.result
                    downloadUrls.add(url.toString())
                    if (downloadUrls.size == imgUri.size){
                        _isImgsUploaded.value = true
                        _downloadedUrls.value = downloadUrls
                    }
                }
        }
    }

    fun saveProduct(product: Product) {
        val getInstance= FirebaseDatabase.getInstance()
        getInstance.getReference("Admins").child("products/${product.productId}")
            .setValue(product).addOnSuccessListener{
                                _isProductSaved.value = true
                            }
            .addOnFailureListener {

            }
    }

    fun fetchProducts(adminUid: String?, categoryTitle: String?): Flow<List<Product>> = callbackFlow {
        val dbPath = FirebaseDatabase.getInstance().getReference("Admins").child("products")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for (product in snapshot.children) {
                    val prod = product.getValue(Product::class.java)
                    if (product != null) {
                        if (prod?.adminUid == adminUid && (categoryTitle == "All" || prod?.productCategory == categoryTitle)) {
                            products.add(prod!!)
                        }
                    }
                }
                trySend(products)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        dbPath.addValueEventListener(eventListener)
        awaitClose{dbPath.removeEventListener(eventListener)}
    }

    fun removeProductFromFirestore(productId: String, onComplete: (Boolean) -> Unit) {
//            Log.d("removeProduct", "Attempting to remove product from Realtime Database: $productId")

            // Reference to Realtime Database
            val realtimeDb = FirebaseDatabase.getInstance()
            realtimeDb.getReference("Admins").child("products").child(productId)
                .removeValue()
                .addOnSuccessListener {
                    Log.d("removeProduct", "Product successfully removed from Realtime Database: $productId")
                    onComplete(true)
                }
                .addOnFailureListener {
                    Log.d("removeProduct", "Error removing product from Realtime Database: $productId")
                    onComplete(false)
                }
    }

    fun fetchOrdersForAdmin(adminId: String): Flow<List<Pair<String, OrderDetailz>>> = callbackFlow {
        val ordersRef = FirebaseFirestore.getInstance().collection("orders")
        Log.d("AVM---1fetchOrdersForAdmin", ordersRef.toString())

        // Use addSnapshotListener for Firestore
        val eventListener = ordersRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("AdminVM", "Error fetching orders: ${error.message}")
                return@addSnapshotListener
            }

            val orders = mutableListOf<Pair<String, OrderDetailz>>()
            Log.d("AVM---2fetchOrdersForAdmin", "$snapshot   size is ${snapshot?.size()}")


            snapshot?.documents?.forEach { orderSnapshot ->
                val order = orderSnapshot.toObject(OrderDetailz::class.java)
                Log.d("AVM---3fetchOrdersForAdmin", order.toString())
                val orderId = orderSnapshot.id // This is the Firestore document ID

                // Filter the order details to include only items that match the adminId
                val filteredOrderDetails = order?.orderDetails?.filter { it.adminId?.trim() == adminId?.trim() }

                // Only add the order if there are matching items for the admin
                if (!filteredOrderDetails.isNullOrEmpty()) {
                    // Create a new order with only the filtered order details
                    val filteredOrder = order.copy(orderDetails = filteredOrderDetails)
                    orders.add(Pair(orderId, filteredOrder)) // Add the filtered order
                }
            }

            trySend(orders) // Send the orders back through the flow
        }

        // Make sure to close the listener when the flow is closed
        awaitClose { eventListener.remove() }
    }
}