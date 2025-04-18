package com.group.FlyNest.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import com.group.FlyNest.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OfferFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OfferFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
=======
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.group.FlyNest.R

class OfferFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var moreOptionsButton: FloatingActionButton
    private val allOffers = mutableListOf<CouponOffer>()
    private lateinit var allAdapter: AllCouponAdapter
    private lateinit var claimedAdapter: ClaimedCouponAdapter
    private lateinit var expiredAdapter: ExpiredCouponAdapter
>>>>>>> upstream/main

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
<<<<<<< HEAD
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OfferFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OfferFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
=======
        return inflater.inflate(R.layout.fragment_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupRecyclerView()
        setupClickListeners()
    }

    private fun initializeViews(view: View) {
        recyclerView = view.findViewById(R.id.offers_recycler_view)
            ?: throw IllegalStateException("RecyclerView not found")
        moreOptionsButton = view.findViewById(R.id.more_options_button)
            ?: throw IllegalStateException("More options button not found")
    }

    private fun setupRecyclerView() {
        allOffers.clear()
        allOffers.addAll(getSampleOffers())
        allAdapter = AllCouponAdapter(allOffers.filter { it.status == CouponStatus.ALL }) { coupon ->
            Toast.makeText(context, "Redeeming: ${coupon.code}", Toast.LENGTH_SHORT).show()
            coupon.status = CouponStatus.CLAIMED
            updateAdapters()
        }
        claimedAdapter = ClaimedCouponAdapter(allOffers.filter { it.status == CouponStatus.CLAIMED })
        expiredAdapter = ExpiredCouponAdapter(allOffers.filter { it.status == CouponStatus.EXPIRED })
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allAdapter // Default to "All" view
        }
    }

    private fun setupClickListeners() {
        moreOptionsButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 1, 0, "All Coupons")
        popup.menu.add(0, 2, 1, "Claimed Coupons")
        popup.menu.add(0, 3, 2, "Expired Coupons")
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> {
                    recyclerView.adapter = allAdapter
                    Toast.makeText(context, "Showing All", Toast.LENGTH_SHORT).show()
                }
                2 -> {
                    recyclerView.adapter = claimedAdapter
                    Toast.makeText(context, "Showing Claimed", Toast.LENGTH_SHORT).show()
                }
                3 -> {
                    recyclerView.adapter = expiredAdapter
                    Toast.makeText(context, "Showing Expired", Toast.LENGTH_SHORT).show()
                }
                else -> false
            }
            true
        }
        popup.show()
    }

    private fun updateAdapters() {
        allAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.ALL })
        claimedAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.CLAIMED })
        expiredAdapter.updateOffers(allOffers.filter { it.status == CouponStatus.EXPIRED })
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun getSampleOffers(): List<CouponOffer> {
        return listOf(
            CouponOffer(
                title = "20% Off Flights",
                description = "Valid until May 31, 2025",
                imageUrl = null,
                imageResId = R.drawable.percent_discount,
                code = "FLY20",
                type = CouponType.PERCENTAGE_DISCOUNT,
                status = CouponStatus.ALL
            ),
            CouponOffer(
                title = "$50 Off Hotels",
                description = "Book by April 30, 2025",
                imageUrl = null,
                imageResId = R.drawable.fixed_discount,
                code = "HOTEL50",
                type = CouponType.FIXED_AMOUNT_DISCOUNT,
                status = CouponStatus.CLAIMED
            ),
            CouponOffer(
                title = "Free Airport Transfer",
                description = "For bookings over $200",
                imageUrl = "https://example.com/transfer.jpg",
                imageResId = null,
                code = "FREE200",
                type = CouponType.FREE_ITEM,
                status = CouponStatus.EXPIRED
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance() = OfferFragment()
    }
}

data class CouponOffer(
    val title: String,
    val description: String,
    val imageUrl: String?,
    val imageResId: Int?,
    val code: String,
    val type: CouponType,
    var status: CouponStatus = CouponStatus.ALL
)

enum class CouponType {
    PERCENTAGE_DISCOUNT,
    FIXED_AMOUNT_DISCOUNT,
    FREE_ITEM
}

enum class CouponStatus {
    ALL,
    CLAIMED,
    EXPIRED
>>>>>>> upstream/main
}