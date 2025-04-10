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
<<<<<<< HEAD
            duration.text = formatDuration(flight.duration) // Fixed: Convert Int to String
=======
            duration.text = flight.duration
>>>>>>> upstream/main
            price.text = "RM${flight.price}"
            stops.text = if (flight.stops == 0) "Non-stop" else "${flight.stops} stop(s)"
            airlineLogo.setImageResource(flight.airlineLogo)
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
<<<<<<< HEAD
            .setPositiveButton("Book Now") { _, _ ->
                // Add booking action here if needed
            }
            .setNegativeButton("Close") { _, _ ->
                dismiss() // Close the dialog
            }
            .create()
    }

    private fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}h ${remainingMinutes}m"
        } else {
            "${remainingMinutes}m"
        }
    }
=======
            .setPositiveButton("Book Now") { _, _ -> }
            .setNegativeButton("Close") { _, _ -> }
            .create()
    }
>>>>>>> upstream/main
}