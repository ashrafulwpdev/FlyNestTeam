package com.group.FlyNest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
<<<<<<< HEAD
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.group.FlyNest.databinding.ActivityPassengerInfoBinding
import com.group.FlyNest.databinding.ItemPassengerFormBinding
=======
import androidx.appcompat.app.AppCompatActivity
import com.group.FlyNest.databinding.ActivityPassengerInfoBinding
>>>>>>> upstream/main
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import java.text.SimpleDateFormat
import java.util.*

class PassengerInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPassengerInfoBinding
    private lateinit var flight: Flight
    private var passengersCount: Int = 1
    private var seatClass: String = "Economy"
<<<<<<< HEAD
    private val passengersList = ArrayList<Passenger>()
    private lateinit var passengerAdapter: PassengerFormAdapter
    private lateinit var auth: FirebaseAuth
=======
    private var currentPassengerIndex = 1
    private val passengerList = mutableListOf<Passenger>()
>>>>>>> upstream/main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPassengerInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            finish()
            return
        }

=======
>>>>>>> upstream/main
        flight = intent.getParcelableExtra(EXTRA_FLIGHT) ?: run {
            finish()
            return
        }
        passengersCount = intent.getIntExtra("passengers", 1)
        seatClass = intent.getStringExtra("seatClass") ?: "Economy"

        setupToolbar()
        displayFlightInfo()
<<<<<<< HEAD
        setupPassengerForms()
=======
        setupPassengerForm()
>>>>>>> upstream/main
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
<<<<<<< HEAD
            title = "Passenger Details (${passengersCount}x $seatClass)"
        }
=======
            setDisplayShowHomeEnabled(true)
            title = "Passenger Details (${passengersCount}x $seatClass)"
        }

>>>>>>> upstream/main
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

<<<<<<< HEAD
    private fun setupPassengerForms() {
        binding.passengerFormsRecyclerView.layoutManager = LinearLayoutManager(this)
        passengerAdapter = PassengerFormAdapter(passengersCount)
        binding.passengerFormsRecyclerView.adapter = passengerAdapter
=======
    private fun setupPassengerForm() {
        binding.passengerTitle.text = "Passenger ($currentPassengerIndex of $passengersCount)"
>>>>>>> upstream/main
    }

    private fun setupListeners() {
        binding.submitButton.setOnClickListener {
<<<<<<< HEAD
            if (validateForms()) {
                passengersList.clear()
                passengersList.addAll(passengerAdapter.getPassengers())
                navigateToPayment()
=======
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
>>>>>>> upstream/main
            }
        }
    }

<<<<<<< HEAD
    private fun navigateToPayment() {
        Intent(this, PaymentActivity::class.java).apply {
            putExtra(EXTRA_FLIGHT, flight)
            putParcelableArrayListExtra("passengers", passengersList)
            putExtra("passengersCount", passengersCount)
            putExtra("seatClass", seatClass)
            putExtra("totalPrice", flight.price * passengersCount)
            startActivity(this)
        }
    }

    private fun validateForms(): Boolean {
        return passengerAdapter.validateAllForms()
    }

    inner class PassengerFormAdapter(private val passengerCount: Int) :
        RecyclerView.Adapter<PassengerFormAdapter.PassengerViewHolder>() {

        private val passengerViews = mutableListOf<ItemPassengerFormBinding>()

        inner class PassengerViewHolder(val binding: ItemPassengerFormBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
            val binding = ItemPassengerFormBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            passengerViews.add(binding)
            return PassengerViewHolder(binding)
        }

        override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
            holder.binding.passengerTitle.text = "Passenger ${position + 1} of $passengerCount"
            if (position == 0) {
                holder.binding.passengerTitle.text = "Primary Passenger (1 of $passengerCount)"
            }
            // flightCard and submitButton are now in the activity layout, not here
        }

        override fun getItemCount(): Int = passengerCount

        fun getPassengers(): List<Passenger> {
            return passengerViews.mapIndexed { index, binding ->
                Passenger(
                    name = binding.nameInput.text.toString().trim(),
                    email = binding.emailInput.text.toString().trim(),
                    phone = binding.phoneInput.text.toString().trim(),
                    passport = binding.passportInput.text.toString().trim(),
                    isPrimary = index == 0
                )
            }
        }

        fun validateAllForms(): Boolean {
            var isValid = true
            passengerViews.forEach { binding ->
                if (binding.nameInput.text.isNullOrBlank()) {
                    binding.nameContainer.error = "Full name is required"
                    isValid = false
                } else {
                    binding.nameContainer.error = null
                }

                if (binding.emailInput.text.isNullOrBlank()) {
                    binding.emailContainer.error = "Email is required"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailInput.text.toString()).matches()) {
                    binding.emailContainer.error = "Please enter a valid email"
                    isValid = false
                } else {
                    binding.emailContainer.error = null
                }

                if (binding.phoneInput.text.isNullOrBlank()) {
                    binding.phoneContainer.error = "Phone number is required"
                    isValid = false
                } else if (binding.phoneInput.text.toString().length < 10) {
                    binding.phoneContainer.error = "Enter a valid phone number (min 10 digits)"
                    isValid = false
                } else {
                    binding.phoneContainer.error = null
                }

                if (binding.passportInput.text.isNullOrBlank()) {
                    binding.passportContainer.error = "Passport number is required"
                    isValid = false
                } else if (binding.passportInput.text.toString().length < 8) {
                    binding.passportContainer.error = "Passport number too short"
                    isValid = false
                } else {
                    binding.passportContainer.error = null
                }
            }
            return isValid
        }
=======
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
>>>>>>> upstream/main
    }

    companion object {
        const val EXTRA_FLIGHT = "flight"
<<<<<<< HEAD
    }
}
=======
        const val EXTRA_PASSENGER_LIST = "passenger_list"
    }
}
>>>>>>> upstream/main
