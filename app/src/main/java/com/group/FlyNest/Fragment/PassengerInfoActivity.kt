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
    private var currentPassengerIndex = 1
    private val passengerList = mutableListOf<Passenger>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Passenger Details (${passengersCount}x $seatClass)"
        }

        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun displayFlightInfo() {
        binding.apply {
            airlineName.text = flight.airline
            flightNumber.text = flight.flightNumber
            routeText.text = "${flight.departureAirport} → ${flight.arrivalAirport}"
            departureDate.text = "${flight.departureTime} • ${formatDate(flight.flightDate)}"
            price.text = "RM${flight.price * passengersCount}"
        }
    }

    private fun formatDate(dateString: String): String {
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
        binding.passengerTitle.text = "Passenger ($currentPassengerIndex of $passengersCount)"
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
            if (validateForm()) {
                val passenger = createPassengerFromInput()
                passengerList.add(passenger)

                if (currentPassengerIndex < passengersCount) {
                    currentPassengerIndex++
                    clearForm()
                    setupPassengerForm()
                } else {
                    navigateToPayment(passengerList)
                }
            }
        }
    }

    private fun createPassengerFromInput(): Passenger {
        return Passenger(
            name = binding.nameInput.text.toString().trim(),
            email = binding.emailInput.text.toString().trim(),
            phone = binding.phoneInput.text.toString().trim(),
            passport = binding.passportInput.text.toString().trim(),
            isPrimary = (currentPassengerIndex == 1) // Only the first is primary
        )
    }

    private fun clearForm() {
        binding.nameInput.text?.clear()
        binding.emailInput.text?.clear()
        binding.phoneInput.text?.clear()
        binding.passportInput.text?.clear()

        binding.nameContainer.error = null
        binding.emailContainer.error = null
        binding.phoneContainer.error = null
        binding.passportContainer.error = null
    }

    private fun navigateToPayment(passengers: List<Passenger>) {
        val intent = Intent(this, PaymentActivity::class.java).apply {
            putExtra(EXTRA_FLIGHT, flight)
            putParcelableArrayListExtra(EXTRA_PASSENGER_LIST, ArrayList(passengers))
            putExtra("passengersCount", passengersCount)
            putExtra("totalPrice", flight.price * passengersCount)
        }
        startActivity(intent)
    }

    private fun validateForm(): Boolean {
        var isValid = true

        with(binding) {
            if (nameInput.text.isNullOrBlank()) {
                nameContainer.error = "Full name is required"
                isValid = false
            } else {
                nameContainer.error = null
            }

            if (emailInput.text.isNullOrBlank()) {
                emailContainer.error = "Email is required"
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.text.toString()).matches()) {
                emailContainer.error = "Please enter a valid email"
                isValid = false
            } else {
                emailContainer.error = null
            }

            if (phoneInput.text.isNullOrBlank()) {
                phoneContainer.error = "Phone number is required"
                isValid = false
            } else if (phoneInput.text.toString().length < 10) {
                phoneContainer.error = "Enter a valid phone number (min 10 digits)"
                isValid = false
            } else {
                phoneContainer.error = null
            }

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
        const val EXTRA_FLIGHT = "flight"
        const val EXTRA_PASSENGER_LIST = "passenger_list"
    }
}
