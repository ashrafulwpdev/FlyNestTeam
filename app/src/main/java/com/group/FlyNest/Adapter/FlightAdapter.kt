package com.group.FlyNest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.group.FlyNest.databinding.ItemFlightBinding
import com.group.FlyNest.model.Flight

class FlightAdapter : ListAdapter<Flight, FlightAdapter.FlightViewHolder>(FlightDiffCallback()) {

    private var itemClickListener: ((Flight) -> Unit)? = null

    fun setOnItemClickListener(listener: (Flight) -> Unit) {
        itemClickListener = listener
    }

    inner class FlightViewHolder(private val binding: ItemFlightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(flight: Flight) {
            binding.apply {
                airlineName.text = flight.airline
                flightNumber.text = flight.flightNumber
                departureTime.text = flight.departureTime
                arrivalTime.text = flight.arrivalTime
                duration.text = flight.duration
                price.text = "RM${flight.price}"
                stops.text = when (flight.stops) {
                    0 -> "Non-stop"
                    1 -> "1 stop"
                    else -> "${flight.stops} stops"
                }
                airlineLogo.setImageResource(flight.airlineLogo)

                root.setOnClickListener { itemClickListener?.invoke(flight) }
                bookButton.setOnClickListener { itemClickListener?.invoke(flight) }
            }
        }
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