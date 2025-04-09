package com.group.FlyNest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group.FlyNest.R
import com.group.FlyNest.adapter.FlightAdapter
import com.group.FlyNest.databinding.FragmentFlightResultsBinding
import com.group.FlyNest.model.Flight
import java.text.SimpleDateFormat
import java.util.*

class FlightResultsFragment : Fragment() {

    private var _binding: FragmentFlightResultsBinding? = null
    private val binding get() = _binding!!
    private lateinit var flightAdapter: FlightAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlightResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get search parameters
        val args = arguments ?: Bundle()
        val origin = args.getString("origin", "")
        val destination = args.getString("destination", "")
        val date = args.getString("date", "")
        val passengers = args.getInt("passengers", 1)
        val seatClass = args.getString("seatClass", "Economy")

        // Set header info
        binding.routeText.text = "$origin to $destination"
        binding.dateText.text = formatDate(date)
        binding.passengersText.text = "$passengers ${if (passengers > 1) "passengers" else "passenger"} • $seatClass"

        setupRecyclerView()
        loadFlights(origin, destination, date)
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

    private fun setupRecyclerView() {
        flightAdapter = FlightAdapter()
        binding.flightsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = flightAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadFlights(origin: String, destination: String, date: String) {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE

        // Simulate network delay
        binding.flightsRecyclerView.postDelayed({
            val flights = generateSampleFlights(origin, destination, date)

            if (flights.isEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.flightsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.flightsRecyclerView.visibility = View.VISIBLE
                flightAdapter.submitList(flights)
            }

            binding.loadingIndicator.visibility = View.GONE
        }, 1500)
    }

    private fun generateSampleFlights(origin: String, destination: String, date: String): List<Flight> {
        return listOf(
            Flight(
                airline = "Malaysia Airlines",
                flightNumber = "MH${(1000..9999).random()}",
                departureTime = "08:00",
                arrivalTime = "10:30",
                duration = "2h 30m",
                price = (250..400).random(),
                stops = 0,
                airlineLogo = R.drawable.logo_malaysia_airlines,
                departureAirport = origin,
                arrivalAirport = destination,
                flightDate = date
            ),
            Flight(
                airline = "AirAsia",
                flightNumber = "AK${(1000..9999).random()}",
                departureTime = "11:45",
                arrivalTime = "14:15",
                duration = "2h 30m",
                price = (150..300).random(),
                stops = 0,
                airlineLogo = R.drawable.logo_airasia,
                departureAirport = origin,
                arrivalAirport = destination,
                flightDate = date
            ),
            Flight(
                airline = "Batik Air",
                flightNumber = "ID${(1000..9999).random()}",
                departureTime = "15:20",
                arrivalTime = "18:50",
                duration = "3h 30m",
                price = (300..450).random(),
                stops = 1,
                airlineLogo = R.drawable.logo_batik_air,
                departureAirport = origin,
                arrivalAirport = destination,
                flightDate = date
            ),
            Flight(
                airline = "Firefly",
                flightNumber = "FY${(1000..9999).random()}",
                departureTime = "06:15",
                arrivalTime = "07:45",
                duration = "1h 30m",
                price = (200..350).random(),
                stops = 0,
                airlineLogo = R.drawable.logo_firefly,
                departureAirport = origin,
                arrivalAirport = destination,
                flightDate = date
            )
        ).sortedBy { it.price } // Sort by price (lowest first)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}