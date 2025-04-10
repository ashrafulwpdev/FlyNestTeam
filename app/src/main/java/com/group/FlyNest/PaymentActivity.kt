package com.group.FlyNest.activity

<<<<<<< HEAD
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
=======
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
>>>>>>> upstream/main
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
<<<<<<< HEAD
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group.FlyNest.databinding.ActivityPaymentBinding
import com.group.FlyNest.databinding.ItemPassengerSummaryBinding
=======
import com.group.FlyNest.databinding.ActivityPaymentBinding
>>>>>>> upstream/main
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import com.group.FlyNest.util.CardUtils
import java.text.SimpleDateFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var isProcessingPayment = false
<<<<<<< HEAD
    private lateinit var flight: Flight
    private lateinit var passengers: ArrayList<Passenger>
    private var passengersCount: Int = 1
    private var seatClass: String = "Economy"
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val malaysianBanks = listOf(
        "Maybank", "CIMB Bank", "Public Bank", "RHB Bank", "Hong Leong Bank",
        "AmBank", "Bank Rakyat", "Bank Islam", "OCBC Bank", "HSBC Bank"
=======

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
>>>>>>> upstream/main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        flight = intent.getParcelableExtra("flight") ?: run {
            Toast.makeText(this, "Flight data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        passengers = intent.getParcelableArrayListExtra("passengers") ?: run {
            Toast.makeText(this, "Passenger data not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        passengersCount = intent.getIntExtra("passengersCount", 1)
        seatClass = intent.getStringExtra("seatClass") ?: "Economy"

        setupUI()
        setupPassengerSummary()
        setupPaymentMethodSelection()
        setupFormValidation()
        setupPayNowButton()
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Payment"
        }
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI() {
        binding.flightInfo.text = "Flight: ${flight.departureAirport} → ${flight.arrivalAirport}"
        binding.departureDate.text = "Date: ${formatFlightDate(flight.flightDate)}"
        binding.price.text = "Amount: RM ${String.format("%.2f", (flight.price * passengersCount).toDouble())}"

        val bankAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, malaysianBanks)
        binding.bankSpinner.setAdapter(bankAdapter)
        binding.bankSpinner.setOnClickListener { binding.bankSpinner.showDropDown() }
    }

    private fun setupPassengerSummary() {
        binding.passengerRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.passengerRecyclerView.adapter = PassengerSummaryAdapter(passengers)
=======
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
>>>>>>> upstream/main
    }

    private fun formatFlightDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEE, MMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
<<<<<<< HEAD
            outputFormat.format(date ?: return dateString)
=======
            outputFormat.format(date!!)
>>>>>>> upstream/main
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupPaymentMethodSelection() {
<<<<<<< HEAD
        binding.paymentCard.isChecked = true
        showPaymentDetails(binding.paymentCard.id)

=======
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
>>>>>>> upstream/main
        binding.paymentMethodGroup.setOnCheckedChangeListener { _, checkedId ->
            showPaymentDetails(checkedId)
        }
    }

    private fun showPaymentDetails(checkedId: Int) {
<<<<<<< HEAD
=======
        // Hide all payment detail layouts first
>>>>>>> upstream/main
        binding.cardDetailsLayout.visibility = View.GONE
        binding.paypalDetailsLayout.visibility = View.GONE
        binding.bankDetailsLayout.visibility = View.GONE

<<<<<<< HEAD
        when (checkedId) {
            binding.paymentCard.id -> binding.cardDetailsLayout.visibility = View.VISIBLE
            binding.paymentPaypal.id -> binding.paypalDetailsLayout.visibility = View.VISIBLE
            binding.paymentBankTransfer.id -> binding.bankDetailsLayout.visibility = View.VISIBLE
=======
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
>>>>>>> upstream/main
        }
    }

    private fun setupFormValidation() {
<<<<<<< HEAD
        binding.cardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val clean = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in clean.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(clean[i])
                }
                s?.replace(0, s.length, formatted.toString())
                isFormatting = false
            }
        })

        binding.cardExpiryDate.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true
                val clean = s.toString().replace("/", "")
                if (clean.length >= 2) {
                    val month = clean.substring(0, 2)
                    val monthInt = month.toIntOrNull() ?: 0
                    if (monthInt in 1..12) {
                        val formatted = if (clean.length > 2) "$month/${clean.substring(2, minOf(4, clean.length))}" else "$month/"
                        s?.replace(0, s.length, formatted)
                    } else {
                        binding.cardExpiryDate.error = "Invalid month"
                    }
                }
                isFormatting = false
=======
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
>>>>>>> upstream/main
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
<<<<<<< HEAD
        when (paymentMethod) {
=======
        return when (paymentMethod) {
>>>>>>> upstream/main
            "card" -> {
                val cardNumber = binding.cardNumber.text.toString().replace(" ", "")
                val expiry = binding.cardExpiryDate.text.toString()
                val cvv = binding.cardCVV.text.toString()

                if (cardNumber.length < 16 || !CardUtils.isValidCardNumber(cardNumber)) {
                    binding.cardNumber.error = "Invalid card number"
                    return false
                }

<<<<<<< HEAD
                val (month, year) = expiry.split("/").let { it.getOrNull(0) to it.getOrNull(1) }
                val monthInt = month?.toIntOrNull()
                if (monthInt !in 1..12 || year?.length != 2) {
=======
                if (expiry.length != 5 || !expiry.contains("/")) {
>>>>>>> upstream/main
                    binding.cardExpiryDate.error = "Invalid expiry date (MM/YY)"
                    return false
                }

<<<<<<< HEAD
                val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                val expiryYear = year?.toIntOrNull() ?: 0
                if (expiryYear < currentYear || (expiryYear == currentYear && monthInt!! < currentMonth)) {
                    binding.cardExpiryDate.error = "Card has expired"
                    return false
                }

=======
>>>>>>> upstream/main
                if (cvv.length != 3) {
                    binding.cardCVV.error = "CVV must be 3 digits"
                    return false
                }
<<<<<<< HEAD
=======

                true
>>>>>>> upstream/main
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
<<<<<<< HEAD
            }
            "bank_transfer" -> {
                val bank = binding.bankSpinner.text.toString()
                val username = binding.bankUsername.text.toString()
                val password = binding.bankPassword.text.toString()

                if (bank.isEmpty() || bank !in malaysianBanks) {
                    Toast.makeText(this, "Please select a valid bank", Toast.LENGTH_SHORT).show()
=======

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
>>>>>>> upstream/main
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
<<<<<<< HEAD
            }
        }
        return true
    }

    private fun processPayment(paymentMethod: String) {
        isProcessingPayment = true
        binding.payNowButton.isEnabled = false
        binding.payNowButton.visibility = View.GONE
        binding.paymentProgressBar.visibility = View.VISIBLE

        val bookingReference = "FLY${UUID.randomUUID().toString().substring(0, 8).uppercase()}"
        val gate = "G${(1..30).random()}"
        val seat = "${(1..30).random()}${(('A'..'F').toList()).random()}"

        val booking = hashMapOf(
            "flight" to flight,
            "passengers" to passengers,
            "bookingReference" to bookingReference,
            "gate" to gate,
            "seat" to seat,
            "seatClass" to seatClass,
            "passengersCount" to passengersCount,
            "timestamp" to System.currentTimeMillis(),
            "paymentMethod" to paymentMethod,
            "status" to "confirmed"
        )

        firestore.collection("users")
            .document(auth.currentUser!!.uid)
            .collection("bookings")
            .document(bookingReference)
            .set(booking)
            .addOnSuccessListener {
                binding.payNowButton.isEnabled = true
                binding.payNowButton.visibility = View.VISIBLE
                binding.paymentProgressBar.visibility = View.GONE
                isProcessingPayment = false

                Toast.makeText(this, "Payment processed successfully!", Toast.LENGTH_SHORT).show()

                Intent(this, BoardingPassActivity::class.java).apply {
                    putExtra("flight", flight)
                    putExtra("passenger", passengers[0])
                    putExtra("bookingReference", bookingReference)
                    putExtra("gate", gate)
                    putExtra("seat", seat)
                    putExtra("seatClass", seatClass)
                    putExtra("passengersCount", passengersCount)
                    startActivity(this)
                    finish()
                }
            }
            .addOnFailureListener { e ->
                binding.payNowButton.isEnabled = true
                binding.payNowButton.visibility = View.VISIBLE
                binding.paymentProgressBar.visibility = View.GONE
                isProcessingPayment = false
                Toast.makeText(this, "Error processing payment: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    inner class PassengerSummaryAdapter(private val passengers: List<Passenger>) :
        RecyclerView.Adapter<PassengerSummaryAdapter.PassengerViewHolder>() {

        inner class PassengerViewHolder(val binding: ItemPassengerSummaryBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
            val binding = ItemPassengerSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PassengerViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
            val passenger = passengers[position]
            holder.binding.passengerName.text = "Passenger ${position + 1}: ${passenger.name}"
            holder.binding.passengerEmail.text = "Email: ${passenger.email}"
            holder.binding.passengerPhone.text = "Phone: ${passenger.phone}"
        }

        override fun getItemCount(): Int = passengers.size
    }

    companion object {
        const val EXTRA_FLIGHT = "flight"
    }
}
=======

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
>>>>>>> upstream/main
