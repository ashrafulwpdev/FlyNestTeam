package com.group.FlyNest.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.group.FlyNest.databinding.FragmentFaqBinding
import com.group.FlyNest.databinding.ItemFaqBinding
import androidx.recyclerview.widget.RecyclerView

class FAQFragment : Fragment() {

    private var _binding: FragmentFaqBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaqBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Set up RecyclerView with FAQ list
        binding.faqRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.faqRecyclerView.adapter = FAQAdapter(getSampleFAQs())
    }

    // Sample FAQ data
    private fun getSampleFAQs(): List<FAQItem> {
        return listOf(
            FAQItem("How do I book a flight?", "Go to the Booking tab, enter your details, and select a flight."),
            FAQItem("Can I cancel my booking?", "Yes, cancellations are available under certain conditions. Check our policy."),
            FAQItem("How do I contact support?", "Use the Contact Us section in the app or email us at support@flynest.com."),
            FAQItem("What payment methods are accepted?", "We accept credit/debit cards and select digital wallets.")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Data class for FAQ items
data class FAQItem(val question: String, val answer: String)

// RecyclerView Adapter for FAQ items
class FAQAdapter(private val faqList: List<FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val binding = ItemFaqBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FAQViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faq = faqList[position]
        holder.bind(faq)
    }

    override fun getItemCount(): Int = faqList.size

    class FAQViewHolder(private val binding: ItemFaqBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(faq: FAQItem) {
            binding.questionText.text = faq.question
            binding.answerText.text = faq.answer
        }
    }
}