package com.group.FlyNest.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.group.FlyNest.R
import com.group.FlyNest.databinding.FragmentBookingBinding
import com.group.FlyNest.model.Flight
import com.group.FlyNest.model.Passenger
import java.text.SimpleDateFormat
import java.util.*

class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up Toolbar with back button
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack() // Navigate back
        }

        // Set up ViewPager2 with TabLayout
        val pagerAdapter = BookingPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Current Booking"
                1 -> "Completed Booking"
                else -> null
            }
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ViewPager2 adapter for tabs
    private inner class BookingPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> BookingListFragment.newInstance(isCurrent = true)
                1 -> BookingListFragment.newInstance(isCurrent = false)
                else -> throw IllegalStateException("Invalid position")
            }
        }
    }
}

// Fragment to display a list of bookings (Current or Completed)
class BookingListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private val bookings = mutableListOf<Booking>()
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val ARG_IS_CURRENT = "isCurrent"

        fun newInstance(isCurrent: Boolean): BookingListFragment {
            return BookingListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_CURRENT, isCurrent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_booking_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        bookingAdapter = BookingAdapter(bookings)
        recyclerView.adapter = bookingAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isCurrent = arguments?.getBoolean(ARG_IS_CURRENT) ?: true
        fetchBookings(isCurrent)
    }

    private fun fetchBookings(isCurrent: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        val currentTime = System.currentTimeMillis()

        firestore.collection("users")
            .document(userId)
            .collection("bookings")
            .get()
            .addOnSuccessListener { result ->
                bookings.clear()
                for (document in result) {
                    val booking = document.toObject(Booking::class.java)
                    val flightDate = parseDate(booking.flight.flightDate)
                    val isBookingCurrent = (flightDate?.time ?: 0) >= currentTime
                    if (isCurrent == isBookingCurrent) {
                        bookings.add(booking)
                    }
                }
                bookingAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error fetching bookings: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            inputFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}

// Data class for a booking
data class Booking(
    val flight: Flight = Flight("", "", "", "", "", "", "", 0, 0, 0),
    val passengers: List<Passenger> = emptyList(),
    val bookingReference: String = "",
    val gate: String = "",
    val seat: String = "",
    val seatClass: String = "",
    val passengersCount: Int = 1,
    val timestamp: Long = 0
)

// RecyclerView Adapter for bookings
class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val airlineLogo: ImageView = itemView.findViewById(R.id.airlineLogo)
        val departureTime: TextView = itemView.findViewById(R.id.departureTime)
        val arrivalTime: TextView = itemView.findViewById(R.id.arrivalTime)
        val departureAirportCode: TextView = itemView.findViewById(R.id.departureAirportCode)
        val arrivalAirportCode: TextView = itemView.findViewById(R.id.arrivalAirportCode)
        val departureAirportName: TextView = itemView.findViewById(R.id.departureAirportName)
        val arrivalAirportName: TextView = itemView.findViewById(R.id.arrivalAirportName)
        val flightDate: TextView = itemView.findViewById(R.id.flightDate)
        val boardingTime: TextView = itemView.findViewById(R.id.boardingTime)
        val flightNumber: TextView = itemView.findViewById(R.id.flightNumber)
        val gateNumber: TextView = itemView.findViewById(R.id.gateNumber)
        val seatNumber: TextView = itemView.findViewById(R.id.seatNumber)
        val flightClass: TextView = itemView.findViewById(R.id.flightClass)
        val modifyButton: Button = itemView.findViewById(R.id.modifyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        val flight = booking.flight

        holder.airlineLogo.setImageResource(flight.airlineLogo)
        holder.departureTime.text = flight.departureTime
        holder.arrivalTime.text = flight.arrivalTime
        holder.departureAirportCode.text = flight.departureAirport
        holder.arrivalAirportCode.text = flight.arrivalAirport
        holder.departureAirportName.text = getAirportName(flight.departureAirport)
        holder.arrivalAirportName.text = getAirportName(flight.arrivalAirport)
        holder.flightDate.text = formatFlightDate(flight.flightDate)
        holder.boardingTime.text = calculateBoardingTime(flight.departureTime)
        holder.flightNumber.text = flight.flightNumber
        holder.gateNumber.text = booking.gate
        holder.seatNumber.text = booking.seat
        holder.flightClass.text = booking.seatClass

        holder.modifyButton.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "Modify booking: ${booking.bookingReference}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getItemCount(): Int = bookings.size

    private fun getAirportName(airportCode: String): String {
        return when (airportCode) {
            "DEL" -> "Indira Gandhi International Airport"
            "CCU" -> "Subhash Chandra Bose International"
            else -> "$airportCode Airport"
        }
    }

    private fun formatFlightDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun calculateBoardingTime(departureTime: String): String {
        return try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val departureDate = timeFormat.parse(departureTime)
            val calendar = Calendar.getInstance().apply {
                time = departureDate!!
                add(Calendar.MINUTE, -30)
            }
            timeFormat.format(calendar.time)
        } catch (e: Exception) {
            "09:30"
        }
    }
}