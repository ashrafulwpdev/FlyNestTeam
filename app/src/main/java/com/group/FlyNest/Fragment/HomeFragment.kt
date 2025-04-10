package com.group.FlyNest.Fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.group.FlyNest.R
import com.group.FlyNest.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Malaysian airports list with codes and names
    private val malaysianAirports = listOf(
        "KUL - Kuala Lumpur International Airport",
        "PEN - Penang International Airport",
        "LGK - Langkawi International Airport",
        "JHB - Senai International Airport",
        "BKI - Kota Kinabalu International Airport",
        "TWU - Tawau Airport",
        "MYY - Miri Airport",
        "KCH - Kuching International Airport",
        "SZB - Sultan Abdul Aziz Shah Airport",
        "IPH - Sultan Azlan Shah Airport"
    )

    private val seatClasses = listOf(
        "Economy Class",
        "Premium Economy",
        "Business Class",
        "First Class"
    )

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var passengerCount = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDatePicker()
        setupPassengerCount()
        setupAutoCompleteAirports()
        setupSeatClassDropdown()
        setupSwapButton()
        setupSearchButton()
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()

        binding.dateInput.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    binding.dateInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis() - 1000
            }.show()
        }
    }

    private fun setupPassengerCount() {
        binding.btnIncrement.setOnClickListener {
            if (passengerCount < 9) {
                passengerCount++
                binding.tvPassengerCount.text = passengerCount.toString()
            }
        }

        binding.btnDecrement.setOnClickListener {
            if (passengerCount > 1) {
                passengerCount--
                binding.tvPassengerCount.text = passengerCount.toString()
            }
        }
    }

    private fun setupAutoCompleteAirports() {
        val airportAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            malaysianAirports
        )

        (binding.originInput as? AutoCompleteTextView)?.apply {
            setAdapter(airportAdapter)
            threshold = 1
            setOnItemClickListener { _, _, position, _ ->
                setText(malaysianAirports[position].substring(0, 3))
            }
        }

        (binding.destinationInput as? AutoCompleteTextView)?.apply {
            setAdapter(airportAdapter)
            threshold = 1
            setOnItemClickListener { _, _, position, _ ->
                setText(malaysianAirports[position].substring(0, 3))
            }
        }
    }

    private fun setupSeatClassDropdown() {
        val seatClassAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            seatClasses
        )

        binding.seatClassInput.apply {
            setAdapter(seatClassAdapter)
            threshold = 1
            setOnClickListener {
                showDropDown()
            }
            setOnItemClickListener { _, _, position, _ ->
                setText(seatClasses[position], false)
            }
            setText(seatClasses[0], false)
        }
    }

    private fun setupSwapButton() {
        binding.btnSwap.setOnClickListener {
            val origin = binding.originInput.text.toString()
            val destination = binding.destinationInput.text.toString()

            binding.originInput.setText(destination)
            binding.destinationInput.setText(origin)

            binding.originLayout.error = null
            binding.destinationLayout.error = null
        }
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val origin = binding.originInput.text.toString().trim()
            val destination = binding.destinationInput.text.toString().trim()
            val date = binding.dateInput.text.toString()
            val seatClass = binding.seatClassInput.text.toString()

            if (validateInputs(origin, destination, date, seatClass)) {
                val originCode = if (origin.length > 3) origin.substring(0, 3) else origin
                val destCode = if (destination.length > 3) destination.substring(0, 3) else destination

                searchFlights(originCode, destCode, date, seatClass, passengerCount)
            }
        }
    }

    private fun validateInputs(
        origin: String,
        destination: String,
        date: String,
        seatClass: String
    ): Boolean {
        binding.originLayout.error = null
        binding.destinationLayout.error = null
        binding.dateLayout.error = null
        binding.seatClassLayout.error = null

        var isValid = true

        // Origin validation
        val originCode = if (origin.length > 3) origin.substring(0, 3) else origin
        if (origin.isBlank()) {
            binding.originLayout.error = "Please select departure airport"
            isValid = false
        } else if (!originCode.matches(Regex("^[A-Z]{3}$"))) {
            binding.originLayout.error = "Invalid airport code"
            isValid = false
        }

        // Destination validation
        val destCode = if (destination.length > 3) destination.substring(0, 3) else destination
        if (destination.isBlank()) {
            binding.destinationLayout.error = "Please select arrival airport"
            isValid = false
        } else if (!destCode.matches(Regex("^[A-Z]{3}$"))) {
            binding.destinationLayout.error = "Invalid airport code"
            isValid = false
        }

        // Check if origin and destination are same
        if (originCode.isNotBlank() && destCode.isNotBlank() && originCode == destCode) {
            binding.originLayout.error = "Cannot fly to the same airport"
            binding.destinationLayout.error = "Cannot fly to the same airport"
            isValid = false
        }

        // Date validation
        if (date.isBlank()) {
            binding.dateLayout.error = "Please select departure date"
            isValid = false
        }

        // Seat class validation
        if (seatClass.isBlank()) {
            binding.seatClassLayout.error = "Please select seat class"
            isValid = false
        }

        return isValid
    }

    private fun searchFlights(
        origin: String,
        destination: String,
        date: String,
        seatClass: String,
        passengers: Int
    ) {
        binding.progressBar.visibility = View.VISIBLE
        Log.d("HomeFragment", "Navigating to flight results with: origin=$origin, destination=$destination, date=$date, seatClass=$seatClass, passengers=$passengers")

        lifecycleScope.launch {
            try {
                // Simulate API call delay
                withContext(Dispatchers.IO) { Thread.sleep(1500) }

                // Create bundle with search parameters
                val bundle = Bundle().apply {
                    putString("origin", origin)
                    putString("destination", destination)
                    putString("date", date)
                    putString("seatClass", seatClass)
                    putInt("passengers", passengers)
                }

                // Navigate to results with parameters
                findNavController().navigate(
                    R.id.action_home_to_flight_results,
                    bundle
                )

            } catch (e: Exception) {
                Log.e("HomeFragment", "Navigation error: ${e.message}")
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}