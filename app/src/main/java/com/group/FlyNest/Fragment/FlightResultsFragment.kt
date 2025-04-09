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
        setupRecyclerView()
        loadFlights()
    }

    private fun setupRecyclerView() {
        flightAdapter = FlightAdapter()
        binding.flightsRecyclerView.apply {  // Changed from recyclerView to flightsRecyclerView
            layoutManager = LinearLayoutManager(requireContext())
            adapter = flightAdapter
        }

        flightAdapter.setOnItemClickListener { flight ->
            FlightDetailsDialog.newInstance(flight).show(
                parentFragmentManager,
                "FlightDetailsDialog"
            )
        }
    }

    private fun loadFlights() {
        binding.progressBar.visibility = View.VISIBLE

        // Sample data - replace with your actual data loading
        val flights = listOf(
            Flight(
                airline = "Malaysia Airlines",
                flightNumber = "MH123",
                departureTime = "08:00",
                arrivalTime = "10:30",
                duration = "2h 30m",
                price = 299,
                stops = 0,
                airlineLogo = R.drawable.logo_malaysia_airlines
            ),
            Flight(
                airline = "AirAsia",
                flightNumber = "AK456",
                departureTime = "11:45",
                arrivalTime = "14:15",
                duration = "2h 30m",
                price = 189,
                stops = 0,
                airlineLogo = R.drawable.logo_airasia
            )
        )

        if (flights.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
        } else {
            binding.emptyState.visibility = View.GONE
            flightAdapter.submitList(flights)
        }

        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}