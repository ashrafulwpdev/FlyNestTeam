package com.group.FlyNest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amadeus.resources.FlightOfferSearch
import com.group.FlyNest.Fragment.DummyFlightOffer

class FlightResultsAdapter(private val offers: List<Any>) :
    RecyclerView.Adapter<FlightResultsAdapter.FlightViewHolder>() {

    class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val priceText: TextView = itemView.findViewById(R.id.price_text)
        val flightInfoText: TextView = itemView.findViewById(R.id.flight_info_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_flight_result, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val offer = offers[position]
        when (offer) {
            is FlightOfferSearch -> {
                holder.priceText.text = "Price: ${offer.price.total}"
                holder.flightInfoText.text = "Flight ${offer.itineraries[0].segments[0].number}: " +
                        "${offer.itineraries[0].segments[0].departure.iataCode} -> " +
                        "${offer.itineraries[0].segments[0].arrival.iataCode}, " +
                        "Duration: ${offer.itineraries[0].duration}"
            }
            is DummyFlightOffer -> {
                holder.priceText.text = "Price: ${offer.totalPrice}"
                holder.flightInfoText.text = "Flight ${offer.flightNumber}: " +
                        "${offer.originCode} -> ${offer.destinationCode}, " +
                        "Duration: ${offer.duration}"
            }
        }
    }

    override fun getItemCount(): Int = offers.size
}