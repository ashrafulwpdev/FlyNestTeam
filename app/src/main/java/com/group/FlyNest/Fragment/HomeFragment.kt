package com.group.FlyNest.Fragment

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.amadeus.Amadeus
import com.amadeus.Params
import com.amadeus.exceptions.NetworkException
import com.amadeus.exceptions.ResponseException
import com.group.FlyNest.R
import com.group.FlyNest.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.Serializable

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var amadeus: Amadeus
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Amadeus client with hardcoded credentials (NOT SECURE - FOR TESTING ONLY)
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

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun searchFlights(origin: String, destination: String, date: String) {
        if (!isNetworkAvailable()) {
            println("Network unavailable - skipping API call")
            Toast.makeText(requireContext(), "No internet connection available", Toast.LENGTH_LONG).show()
            binding.progressBar.visibility = View.GONE
            return
        }

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
                    withRetry(retries = 3, delayMillis = 1000) {
                        println("Attempting API call to test.api.amadeus.com - Origin: $origin, Destination: $destination, Date: $date")
                        try {
                            amadeus.shopping.flightOffersSearch.get(params)
                        } catch (e: Exception) {
                            println("API call failed on attempt: ${e.javaClass.simpleName} - ${e.message}")
                            throw e
                        }
                    }
                }

                println("Flight Offers Size: ${flightOffers.size}")
                flightOffers.forEachIndexed { index, offer ->
                    println("Offer $index: ${offer.price.total}, ${offer.itineraries[0].segments[0].departure.iataCode}")
                }

                if (flightOffers.isNotEmpty()) {
                    val bundle = Bundle().apply {
                        putSerializable("flights", flightOffers as Serializable)
                    }
                    findNavController().navigate(R.id.action_home_to_flight_results, bundle)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "No flights found for $origin to $destination on $date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: NetworkException) {
                println("NetworkException - Message: ${e.message}, Stacktrace: ${e.stackTraceToString()}")
                Toast.makeText(
                    requireContext(),
                    "Network error: Unable to connect to the server. Please check your connection.",
                    Toast.LENGTH_LONG
                ).show()

                // Dummy data fallback only in debug mode
                val isDebugMode = true // Replace with BuildConfig.DEBUG in production
                if (isDebugMode) {
                    println("Falling back to dummy data due to NetworkException")
                    val dummyOffers = arrayOf(
                        DummyFlightOffer(
                            totalPrice = "500.00",
                            flightNumber = "AA123",
                            originCode = origin,
                            destinationCode = destination,
                            departureTime = "$date 10:00:00",
                            arrivalTime = "$date 15:30:00",
                            duration = "PT5H30M"
                        )
                    )
                    val bundle = Bundle().apply {
                        putSerializable("dummy_flights", dummyOffers)
                    }
                    findNavController().navigate(R.id.action_home_to_flight_results, bundle)
                }
            } catch (e: ResponseException) {
                println("ResponseException - Description: ${e.description}, Code: ${e.code}, Cause: ${e.cause}, Stacktrace: ${e.stackTraceToString()}")
                if (e.response != null) {
                    println("Response Body: ${e.response.body}")
                    println("Status Code: ${e.response.statusCode}")
                }
                val errorMessage = when (e.code) {
                    "401" -> "Authentication failed. Please contact support."
                    "400" -> "Invalid input. Please check your airport codes and date."
                    "429" -> "Too many requests. Please try again later."
                    else -> "API Error: ${e.description ?: "Unknown issue"} (Code: ${e.code})"
                }
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()

                // Dummy data fallback only in debug mode
                if (isDebugMode) {
                    println("Falling back to dummy data due to ResponseException")
                    val dummyOffers = arrayOf(
                        DummyFlightOffer(
                            totalPrice = "500.00",
                            flightNumber = "AA123",
                            originCode = origin,
                            destinationCode = destination,
                            departureTime = "$date 10:00:00",
                            arrivalTime = "$date 15:30:00",
                            duration = "PT5H30M"
                        )
                    )
                    val bundle = Bundle().apply {
                        putSerializable("dummy_flights", dummyOffers)
                    }
                    findNavController().navigate(R.id.action_home_to_flight_results, bundle)
                }
            } catch (e: Exception) {
                println("Unexpected Error: ${e.message}, Stacktrace: ${e.stackTraceToString()}")
                Toast.makeText(
                    requireContext(),
                    "Unexpected error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    // Retry logic for transient network failures
    private suspend fun <T> withRetry(
        retries: Int = 3,
        delayMillis: Long = 1000,
        block: suspend () -> T
    ): T {
        var lastException: Exception? = null
        repeat(retries) { attempt ->
            try {
                println("Retry attempt ${attempt + 1} of $retries")
                return block()
            } catch (e: NetworkException) {
                lastException = e
                println("NetworkException on attempt ${attempt + 1}: ${e.message}")
                if (attempt == retries - 1) throw e
                delay(delayMillis)
            } catch (e: Exception) {
                lastException = e
                println("Other exception on attempt ${attempt + 1}: ${e.message}")
                throw e // Non-network exceptions are not retried
            }
        }
        throw lastException ?: IllegalStateException("Retry logic failed without an exception")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}

// Custom Serializable class for dummy flight data
data class DummyFlightOffer(
    val totalPrice: String,
    val flightNumber: String,
    val originCode: String,
    val destinationCode: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String
) : Serializable