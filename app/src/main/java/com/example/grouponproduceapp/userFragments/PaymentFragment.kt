import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.grouponproduceapp.databinding.FragmentPaymentBinding

class PaymentFragment : DialogFragment() {

    private lateinit var binding: FragmentPaymentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)

        // Set up the payment method when the user clicks the "Pay Now" button
        binding.btnProceedToPayment.setOnClickListener {
            val cardInputWidget = binding.cardInputWidget
            val paymentMethodCreateParams = cardInputWidget.paymentMethodCreateParams

            if (paymentMethodCreateParams != null) {
                val bundle = Bundle().apply {
                    putParcelable("paymentMethodCreateParams", paymentMethodCreateParams)
                }
                parentFragmentManager.setFragmentResult("paymentResult", bundle)
                dismiss()  // Dismiss the dialog after payment processing starts
            } else {
                Toast.makeText(requireContext(), "Invalid card details", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
