package com.group.FlyNest.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amadeus.resources.FlightOfferSearch
import com.group.FlyNest.FlightResultsAdapter
import com.group.FlyNest.databinding.FragmentFlightResultsBinding

class FlightResultsFragment : Fragment() {

    private var _binding: FragmentFlightResultsBinding? = null
    private val binding get() = _binding!!

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

        val flightOffers = arguments?.getSerializable("flights") as? Array<FlightOfferSearch>
        val dummyOffers = arguments?.getSerializable("dummy_flights") as? Array<DummyFlightOffer>

        if (flightOffers != null && flightOffers.isNotEmpty()) {
            println("Received ${flightOffers.size} real flight offers")
            setupRecyclerView(flightOffers.toList())
        } else if (dummyOffers != null && dummyOffers.isNotEmpty()) {
            println("Received ${dummyOffers.size} dummy flight offers")
            setupRecyclerView(dummyOffers.toList())
        } else {
            println("No flight offers received in FlightResultsFragment")
            Toast.makeText(context, "No flight data available", Toast.LENGTH_SHORT).show()
            return
        }

        setupBackButton()
    }

    private fun setupRecyclerView(offers: List<Any>) {
        binding.flightsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FlightResultsAdapter(offers) // Adapter must handle both types
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}