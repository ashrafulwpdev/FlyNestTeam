package com.group.FlyNest.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.group.FlyNest.databinding.DialogFlightDetailsBinding
import com.group.FlyNest.model.Flight

class FlightDetailsDialog : DialogFragment() {

    companion object {
        fun newInstance(flight: Flight): FlightDetailsDialog {
            return FlightDetailsDialog().apply {
                arguments = Bundle().apply {
                    putParcelable("flight", flight)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogFlightDetailsBinding.inflate(requireActivity().layoutInflater)
        val flight = arguments?.getParcelable<Flight>("flight")!!

        binding.apply {
            airlineName.text = flight.airline
            flightNumber.text = flight.flightNumber
            departureTime.text = flight.departureTime
            arrivalTime.text = flight.arrivalTime
            duration.text = flight.duration
            price.text = "RM${flight.price}"
            stops.text = if (flight.stops == 0) "Non-stop" else "${flight.stops} stop(s)"
            airlineLogo.setImageResource(flight.airlineLogo)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Book Now") { _, _ -> }
            .setNegativeButton("Close") { _, _ -> }
            .create()
    }
}