package com.example.grouponproduceapp.adminFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.grouponproduceapp.Constants
import com.example.grouponproduceapp.Utils
import com.example.grouponproduceapp.activity.AdminMainActivity
import com.example.grouponproduceapp.adapters.AdapterSelectedImgs
import com.example.grouponproduceapp.models.Product
import com.example.grouponproduceapp.viewmodels.AdminVM
import com.example.grouponproduceapp.R
import com.example.grouponproduceapp.databinding.FragmentAddProductBinding
import kotlinx.coroutines.launch

class AddProductFragment : Fragment() {
    private val viewModel : AdminVM by viewModels()
    lateinit var binding : FragmentAddProductBinding
    private val selectedImgsUri: ArrayList<Uri> = arrayListOf()

    val selectedImgsRegisteration =  registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()){
        listOfUri ->
        val selectedImgs = listOfUri.take(3)
        selectedImgsUri.clear()
        selectedImgsUri.addAll(selectedImgs)
        selectedImgsUri.forEach{
            Log.d("-------uris-------", it.toString())
        }
        binding.rvProductImgs.adapter = AdapterSelectedImgs(selectedImgsUri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        setAutoComplete()
        browseImgs()
        addProductClicked()
        return binding.root

    }

    private fun addProductClicked() {

        binding.apply {
        btnAddProduct.setOnClickListener {
            Utils.showDialog(requireContext(), "Uploading Images")
            val productTitle= etProductTitle.text.toString()
            val productQty= etProductQty.text.toString()
            val productUnit= etProductUnit.text.toString()
            val productPrice= etProductPrice.text.toString()
            val productStock= etProductStock.text.toString()
            val productCategory= etProductCategory.text.toString()
            val productType= etProductType.text.toString()

            if (productType.isEmpty() || productCategory.isEmpty() || productTitle.isEmpty()
                || productUnit.isEmpty() || productQty.isEmpty() || productPrice.isEmpty() || productStock.isEmpty()){
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Empty fields are not allowed!")
            } else if(selectedImgsUri.isEmpty()){
                Utils.hideDialog()
                Utils.showToast(requireContext(), "Upload a few images of the product.")
            } else {
                val product = Product(
                    productTitle= productTitle,
                    productCategory = productCategory,
                    productPrice = productPrice.toInt(),
                    productStock = productStock.toInt(),
                    productQty = productQty.toInt(),
                    productType = productType,
                    productUnit = productUnit,
                    itemCount = 0,
                    adminUid = Utils.getCurrentUserId()
                    )
                saveImgs(product)
            }
        }}
    }

    private fun saveImgs(product: Product) {
        viewModel.saveImgsInDB(selectedImgsUri)
        lifecycleScope.launch {
            viewModel.isImgsUploaded.collect{
                if(it) {
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Images saved.")

                    lifecycleScope.launch {
                        viewModel.downloadedUrls.collect{
                        product.productImgsUri = it
                        saveProduct(product)
                    } }
                }
            }
        }
    }

    private fun saveProduct(product: Product) {
        viewModel.saveProduct(product)
        lifecycleScope.launch {
            viewModel.isProductSaved.collect{
                if (it) {
                    Utils.hideDialog()
                    startActivity(Intent(requireActivity(), AdminMainActivity::class.java))
                    Utils.showToast(requireContext(), "Product is live now.")
                }
            }
        }

    }

    fun setAutoComplete() {
        val units = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allUnits)
        val categories = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allCategories)
        val types = ArrayAdapter(requireContext(), R.layout.show_list, Constants.allTypes)

        binding.apply {
            etProductUnit.setAdapter(units)
            etProductCategory.setAdapter(categories)
            etProductType.setAdapter(types )
        }
    }
    fun browseImgs(){
        binding.btnSelectImg.setOnClickListener{
            selectedImgsRegisteration.launch("image/*")
        }
    }

}