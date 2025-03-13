package com.group.FlyNest.Fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.exceptions.ResponseException
import com.amadeus.resources.FlightOfferSearch
import com.group.FlyNest.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var amadeus: Amadeus
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        amadeus = Amadeus.builder("BXTxSAL4THOZAnRZh6g0FkmjTu7CwKAw", "OuGa0Aurv9ZyeTdt")
            .setHost("test.api.amadeus.com")
            .build()
    }

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

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val origin = binding.originInput.text.toString().trim().uppercase()
            val destination = binding.destinationInput.text.toString().trim().uppercase()
            val date = binding.dateInput.text.toString()

            if (validateInputs(origin, destination, date)) {
                searchFlights(origin, destination, date)
            }
        }
    }

    private fun validateInputs(origin: String, destination: String, date: String): Boolean {
        binding.originLayout.error = null
        binding.destinationLayout.error = null
        binding.dateLayout.error = null

        when {
            origin.isEmpty() -> {
                binding.originLayout.error = "Please enter origin airport code"
                return false
            }
            destination.isEmpty() -> {
                binding.destinationLayout.error = "Please enter destination airport code"
                return false
            }
            date.isEmpty() -> {
                binding.dateLayout.error = "Please select a date"
                return false
            }
            origin == destination -> {
                binding.originLayout.error = "Origin and destination cannot be the same"
                return false
            }
            !origin.matches(Regex("^[A-Z]{3}$")) -> {
                binding.originLayout.error = "Invalid IATA code (3 letters)"
                return false
            }
            !destination.matches(Regex("^[A-Z]{3}$")) -> {
                binding.destinationLayout.error = "Invalid IATA code (3 letters)"
                return false
            }
        }
        return true
    }

    private fun searchFlights(origin: String, destination: String, date: String) {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val params = Params.with("originLocationCode", origin)
                    .and("destinationLocationCode", destination)
                    .and("departureDate", date)
                    .and("adults", "1")
                    .and("nonStop", "false")
                    .and("max", "10")

                val flightOffers = withContext(Dispatchers.IO) {
                    amadeus.shopping.flightOffersSearch.get(params)
                }

                if (flightOffers.isNotEmpty()) {
                    val flightCount = flightOffers.size
                    Toast.makeText(
                        requireContext(),
                        "Found $flightCount flights",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(requireContext(), "No flights found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ResponseException) {
                Toast.makeText(requireContext(), "Error: ${e.description}", Toast.LENGTH_LONG).show()
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
