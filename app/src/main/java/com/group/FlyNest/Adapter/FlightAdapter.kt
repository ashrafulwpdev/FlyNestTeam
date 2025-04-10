package com.group.FlyNest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group.FlyNest.databinding.ItemFlightBinding
import com.group.FlyNest.model.Flight
import java.text.SimpleDateFormat
import java.util.*

class FlightAdapter : ListAdapter<Flight, FlightAdapter.FlightViewHolder>(FlightDiffCallback()) {

    var onItemClickListener: ((Flight) -> Unit)? = null
    var onBookNowClickListener: ((Flight) -> Unit)? = null

    inner class FlightViewHolder(private val binding: ItemFlightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: Flight) {
            with(binding) {
                airlineName.text = flight.airline
                flightNumber.text = flight.flightNumber
                departureTime.text = flight.departureTime
                arrivalTime.text = flight.arrivalTime
<<<<<<< HEAD
                // Convert duration (Int) to a formatted String (e.g., "2h 30m")
                duration.text = formatDuration(flight.duration)
=======
                duration.text = flight.duration
>>>>>>> upstream/main
                price.text = "RM${flight.price}"
                stops.text = when (flight.stops) {
                    0 -> "Non-stop"
                    1 -> "1 stop"
                    else -> "${flight.stops} stops"
                }
                departureAirport.text = flight.departureAirport
                arrivalAirport.text = flight.arrivalAirport
                flightDate.text = formatDate(flight.flightDate)
                airlineLogo.setImageResource(flight.airlineLogo)

                root.setOnClickListener { onItemClickListener?.invoke(flight) }
                bookButton.setOnClickListener { onBookNowClickListener?.invoke(flight) }
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

        private fun formatDuration(minutes: Int): String {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            return if (hours > 0) {
                "$hours"+"h "+"$remainingMinutes"+"m"
            } else {
                "$remainingMinutes"+"m"
            }
        }
=======
>>>>>>> upstream/main
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val binding = ItemFlightBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FlightDiffCallback : DiffUtil.ItemCallback<Flight>() {
        override fun areItemsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem.flightNumber == newItem.flightNumber
        }

        override fun areContentsTheSame(oldItem: Flight, newItem: Flight): Boolean {
            return oldItem == newItem
        }
    }
}