package com.group.FlyNest.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.group.FlyNest.R

class AllCouponAdapter(
    private var offers: List<CouponOffer>,
    private val onRedeemClick: (CouponOffer) -> Unit
) : RecyclerView.Adapter<AllCouponAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: android.widget.ImageView = itemView.findViewById(R.id.coupon_image)
        val titleText: android.widget.TextView = itemView.findViewById(R.id.coupon_title)
        val descriptionText: android.widget.TextView = itemView.findViewById(R.id.coupon_description)
        val codeText: android.widget.TextView = itemView.findViewById(R.id.coupon_code)
        val redeemButton: MaterialButton = itemView.findViewById(R.id.redeem_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_coupon_all, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offer = offers[position]
        holder.titleText.text = offer.title
        holder.descriptionText.text = offer.description
        holder.codeText.text = offer.code

        when {
            offer.imageUrl != null -> {
                Glide.with(holder.itemView.context)
                    .load(offer.imageUrl)
                    .error(R.drawable.ic_launcher_background)
                    .fallback(R.drawable.ic_launcher_background)
                    .into(holder.imageView)
            }
            offer.imageResId != null -> {
                holder.imageView.setImageResource(offer.imageResId)
            }
            else -> {
                val fallbackImage = when (offer.type) {
                    CouponType.PERCENTAGE_DISCOUNT -> R.drawable.percent_discount
                    CouponType.FIXED_AMOUNT_DISCOUNT -> R.drawable.fixed_discount
                    CouponType.FREE_ITEM -> R.drawable.free_item
                }
                holder.imageView.setImageResource(fallbackImage)
            }
        }

        holder.redeemButton.setOnClickListener { onRedeemClick(offer) }
    }

    override fun getItemCount(): Int = offers.size

    fun updateOffers(newOffers: List<CouponOffer>) {
        offers = newOffers
        notifyDataSetChanged()
    }
}