package com.group.FlyNest.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.group.FlyNest.R
import com.group.FlyNest.activity.PassengerInfoActivity
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

        flightAdapter.onBookNowClickListener = { flight ->
            navigateToPassengerInfo(flight)
        }
    }

    private fun navigateToPassengerInfo(flight: Flight) {
        Intent(requireContext(), PassengerInfoActivity::class.java).apply {
            putExtra(PassengerInfoActivity.EXTRA_FLIGHT, flight)
            putExtra("passengers", arguments?.getInt("passengers", 1) ?: 1)
            putExtra("seatClass", arguments?.getString("seatClass") ?: "Economy")
            startActivity(this)
        }
    }

    private fun loadFlights(origin: String, destination: String, date: String) {
        binding.loadingIndicator.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE

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
        val airlines = listOf(
            Triple("Malaysia Airlines", R.drawable.logo_malaysia_airlines, 0),
            Triple("AirAsia", R.drawable.logo_airasia, 1),
            Triple("Malindo Air", R.drawable.logo_malindo, 0),
            Triple("Firefly", R.drawable.logo_firefly, 1),
            Triple("Batik Air", R.drawable.logo_batik_air, 2)
        )

        return airlines.map {
            val (name, logoRes, stops) = it
            Flight(
                airline = name,
                flightNumber = "${name.take(2).uppercase()}${(1000..9999).random()}",
                departureTime = listOf("08:00", "10:45", "14:30", "18:15").random(),
                arrivalTime = listOf("10:30", "13:00", "17:00", "20:30").random(),
                duration = listOf(150, 135, 165).random(), // Fixed: Int values in minutes
                price = (200..500).random(),
                stops = stops,
                airlineLogo = logoRes,
                departureAirport = origin,
                arrivalAirport = destination,
                flightDate = date
            )
        }.sortedBy { it.price }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}