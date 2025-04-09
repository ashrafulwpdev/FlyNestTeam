package com.group.FlyNest.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.group.FlyNest.databinding.ActivityPaymentBinding
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import com.group.FlyNest.util.CardUtils
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var isProcessingPayment = false

    // Malaysian top 10 banks
    private val malaysianBanks = listOf(
        "Maybank",
        "CIMB Bank",
        "Public Bank",
        "RHB Bank",
        "Hong Leong Bank",
        "AmBank",
        "Bank Rakyat",
        "Bank Islam",
        "OCBC Bank",
        "HSBC Bank"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupPaymentMethodSelection()
        setupFormValidation()
        setupPayNowButton()
    }

    private fun setupUI() {
        // Getting Flight and Passenger data from the intent
        val flight = intent.getParcelableExtra<Flight>("flight")
        val passenger = intent.getParcelableExtra<Passenger>("passenger")

        // Populate UI with flight info
        if (flight != null && passenger != null) {
            binding.flightInfo.text = "Flight: ${flight.departureAirport} → ${flight.arrivalAirport}"
            binding.departureDate.text = "Date: ${formatFlightDate(flight.flightDate)}"
            binding.price.text = "Amount: RM ${String.format("%.2f", flight.price.toDouble())}"
        }

        // Set up bank spinner with Malaysian banks
        val bankAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            malaysianBanks
        )
        binding.bankSpinner.setAdapter(bankAdapter)
    }

    private fun formatFlightDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupPaymentMethodSelection() {
        // Set default selection
        binding.paymentCard.isChecked = true
        showPaymentDetails(binding.paymentCard.id)

        // Manually handle clicks on the parent layouts
        listOf(
            binding.paymentCard to binding.paymentCard.id,
            binding.paymentPaypal to binding.paymentPaypal.id,
            binding.paymentBankTransfer to binding.paymentBankTransfer.id
        ).forEach { (radioButton, id) ->
            // Get the parent LinearLayout of each RadioButton
            val parent = radioButton.parent as? ViewGroup
            parent?.setOnClickListener {
                binding.paymentMethodGroup.clearCheck()
                radioButton.isChecked = true
                showPaymentDetails(id)
            }
        }

        // Handle direct radio button clicks
        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            showPaymentDetails(checkedId)
        }
    }

    private fun showPaymentDetails(checkedId: Int) {
        // Hide all payment detail layouts first
        binding.cardDetailsLayout.visibility = View.GONE
        binding.paypalDetailsLayout.visibility = View.GONE
        binding.bankDetailsLayout.visibility = View.GONE

        // Show only the selected payment method details
        when (checkedId) {
            binding.paymentCard.id -> {
                binding.cardDetailsLayout.visibility = View.VISIBLE
            }
            binding.paymentPaypal.id -> {
                binding.paypalDetailsLayout.visibility = View.VISIBLE
            }
            binding.paymentBankTransfer.id -> {
                binding.bankDetailsLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setupFormValidation() {
        // Card number formatting and validation
        binding.cardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    // Format as XXXX XXXX XXXX XXXX
                    if (it.length == 4 || it.length == 9 || it.length == 14) {
                        if (it.length > 19) {
                            it.delete(19, it.length)
                        } else {
                            it.append(" ")
                        }
                    }
                }
            }
        })

        // Expiry date formatting (MM/YY)
        binding.cardExpiryDate.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    val clean = s.toString().replace("[^\\d]".toRegex(), "")
                    var cleanC = current.replace("[^\\d]".toRegex(), "")

                    if (clean.length == 2) {
                        cleanC = clean
                        val month = clean.toInt()
                        if (month > 12) {
                            binding.cardExpiryDate.error = "Invalid month"
                            return
                        }
                        binding.cardExpiryDate.error = null
                        current = "$clean/"
                        binding.cardExpiryDate.setText(current)
                        binding.cardExpiryDate.setSelection(current.length)
                    } else if (clean.length > 2) {
                        val month = clean.substring(0, 2)
                        val monthInt = month.toInt()
                        if (monthInt > 12) {
                            binding.cardExpiryDate.error = "Invalid month"
                            return
                        }
                        binding.cardExpiryDate.error = null
                        current = "$month/${clean.substring(2, 4)}"
                        binding.cardExpiryDate.setText(current)
                        binding.cardExpiryDate.setSelection(current.length)
                    }
                }
            }
        })
    }

    private fun setupPayNowButton() {
        binding.payNowButton.setOnClickListener {
            if (isProcessingPayment) return@setOnClickListener

            val selectedPaymentMethod = when (binding.paymentMethodGroup.checkedRadioButtonId) {
                binding.paymentCard.id -> "card"
                binding.paymentPaypal.id -> "paypal"
                binding.paymentBankTransfer.id -> "bank_transfer"
                else -> ""
            }

            if (selectedPaymentMethod.isEmpty()) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!validatePaymentDetails(selectedPaymentMethod)) {
                return@setOnClickListener
            }

            processPayment(selectedPaymentMethod)
        }
    }

    private fun validatePaymentDetails(paymentMethod: String): Boolean {
        return when (paymentMethod) {
            "card" -> {
                val cardNumber = binding.cardNumber.text.toString().replace(" ", "")
                val expiry = binding.cardExpiryDate.text.toString()
                val cvv = binding.cardCVV.text.toString()

                if (cardNumber.length < 16 || !CardUtils.isValidCardNumber(cardNumber)) {
                    binding.cardNumber.error = "Invalid card number"
                    return false
                }

                if (expiry.length != 5 || !expiry.contains("/")) {
                    binding.cardExpiryDate.error = "Invalid expiry date (MM/YY)"
                    return false
                }

                if (cvv.length != 3) {
                    binding.cardCVV.error = "CVV must be 3 digits"
                    return false
                }

                true
            }
            "paypal" -> {
                val email = binding.paypalEmail.text.toString()
                val password = binding.paypalPassword.text.toString()

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.paypalEmail.error = "Invalid email address"
                    return false
                }

                if (password.length < 6) {
                    binding.paypalPassword.error = "Password must be at least 6 characters"
                    return false
                }

                true
            }
            "bank_transfer" -> {
                // Explicitly casting bankSpinner to Spinner
                val bankSpinner = binding.bankSpinner as android.widget.Spinner
                val bank = bankSpinner.selectedItem.toString()  // Use selectedItem here

                val username = binding.bankUsername.text.toString()
                val password = binding.bankPassword.text.toString()

                if (bank.isEmpty()) {
                    Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show()
                    return false
                }

                if (username.isEmpty()) {
                    binding.bankUsername.error = "Please enter your bank ID or mobile number"
                    return false
                }

                if (password.length < 6) {
                    binding.bankPassword.error = "Password must be at least 6 characters"
                    return false
                }

                true
            }
            else -> false
        }
    }



    private fun processPayment(paymentMethod: String) {
        isProcessingPayment = true
        binding.payNowButton.isEnabled = false
        binding.payNowButton.text = "Processing..."

        // Simulate payment processing (2 seconds delay)
        binding.root.postDelayed({
            isProcessingPayment = false
            binding.payNowButton.isEnabled = true
            binding.payNowButton.text = "Pay Now"

            when (paymentMethod) {
                "card" -> {
                    Toast.makeText(this, "Card payment processed successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                "paypal" -> {
                    Toast.makeText(this, "PayPal payment processed successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                "bank_transfer" -> {
                    Toast.makeText(this, "Bank transfer initiated successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }, 2000)
    }
}
