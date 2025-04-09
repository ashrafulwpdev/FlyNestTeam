package com.group.FlyNest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.group.FlyNest.databinding.ActivityPassengerInfoBinding
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import java.text.SimpleDateFormat
import java.util.*

class PassengerInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassengerInfoBinding
    private lateinit var flight: Flight
    private var passengersCount: Int = 1
    private var seatClass: String = "Economy"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve flight and search parameters
        flight = intent.getParcelableExtra(EXTRA_FLIGHT) ?: run {
            finish()
            return
        }
        passengersCount = intent.getIntExtra("passengers", 1)
        seatClass = intent.getStringExtra("seatClass") ?: "Economy"

        setupToolbar()
        displayFlightInfo()
        setupPassengerForm()
        setupListeners()
    }

    private fun setupToolbar() {
        // Setting the toolbar as the action bar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Passenger Details (${passengersCount}x $seatClass)"
        }

        // Action for the back button in toolbar
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun displayFlightInfo() {
        // Populating the flight details on the screen
        binding.apply {
            airlineName.text = flight.airline
            flightNumber.text = flight.flightNumber
            routeText.text = "${flight.departureAirport} → ${flight.arrivalAirport}"
            departureDate.text = "${flight.departureTime} • ${formatDate(flight.flightDate)}"
            price.text = "RM${flight.price * passengersCount}" // Total for all passengers
        }
    }

    private fun formatDate(dateString: String): String {
        // Format the date from "yyyy-MM-dd" to "MMM d, yyyy"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: return dateString)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupPassengerForm() {
        // Setting up the passenger form title
        binding.passengerTitle.text = "Primary Passenger (1 of $passengersCount)"
    }

    private fun setupListeners() {
        // Button to submit passenger details and navigate to payment screen
        binding.submitButton.setOnClickListener {
            if (validateForm()) {
                val passenger = createPassengerFromInput()
                navigateToPayment(passenger)
            }
        }
    }

    private fun createPassengerFromInput(): Passenger {
        // Creating a Passenger object from the form input
        return Passenger(
            name = binding.nameInput.text.toString().trim(),
            email = binding.emailInput.text.toString().trim(),
            phone = binding.phoneInput.text.toString().trim(),
            passport = binding.passportInput.text.toString().trim(),
            isPrimary = true // Assuming the first passenger is always primary
        )
    }

    private fun navigateToPayment(passenger: Passenger) {
        // Navigating to PaymentActivity with the necessary data
        Intent(this, PaymentActivity::class.java).apply {
            putExtra(EXTRA_FLIGHT, flight)
            putExtra(EXTRA_PASSENGER, passenger)
            putExtra("passengersCount", passengersCount)
            putExtra("totalPrice", flight.price * passengersCount)
            startActivity(this)
        }
    }

    private fun validateForm(): Boolean {
        // Validating all form inputs before submission
        var isValid = true

        with(binding) {
            // Name validation
            if (nameInput.text.isNullOrBlank()) {
                nameContainer.error = "Full name is required"
                isValid = false
            } else {
                nameContainer.error = null
            }

            // Email validation
            if (emailInput.text.isNullOrBlank()) {
                emailContainer.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                emailContainer.error = "Please enter a valid email"
                isValid = false
            } else {
                emailContainer.error = null
            }

            // Phone validation
            if (phoneInput.text.isNullOrBlank()) {
                phoneContainer.error = "Phone number is required"
                isValid = false
            } else if (phoneInput.text.toString().length < 10) {
                phoneContainer.error = "Enter a valid phone number (min 10 digits)"
                isValid = false
            } else {
                phoneContainer.error = null
            }

            // Passport validation
            if (passportInput.text.isNullOrBlank()) {
                passportContainer.error = "Passport number is required"
                isValid = false
            } else if (passportInput.text.toString().length < 8) {
                passportContainer.error = "Passport number too short"
                isValid = false
            } else {
                passportContainer.error = null
            }
        }

        return isValid
    }

    companion object {
        // Constants for intent extras
        const val EXTRA_FLIGHT = "flight"
        const val EXTRA_PASSENGER = "passenger"
    }
}
