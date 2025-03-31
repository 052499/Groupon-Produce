import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.grouponproduceapp.databinding.FragmentPaymentBinding

class PaymentFragment : DialogFragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnProceedToPayment.setOnClickListener {
            processPayment()
        }
    }

    private fun processPayment() {
        val paymentMethodCreateParams = binding.cardInputWidget.paymentMethodCreateParams
        if (paymentMethodCreateParams != null) {
            parentFragmentManager.setFragmentResult(
                "paymentResult", Bundle().apply {
                    putParcelable("paymentMethodCreateParams", paymentMethodCreateParams)
                }
            )
            dismiss()  // Close the dialog after processing
        } else {
            showToast("Invalid card details. Please try again.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

