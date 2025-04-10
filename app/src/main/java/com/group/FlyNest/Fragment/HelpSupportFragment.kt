package com.group.FlyNest.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.group.FlyNest.R
import com.group.FlyNest.databinding.FragmentHelpSupportBinding

class HelpSupportFragment : Fragment() {

    private var _binding: FragmentHelpSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHelpSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up toolbar navigation
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Navigate to FAQFragment when FAQ card is clicked
        binding.faqCard.setOnClickListener {
            findNavController().navigate(R.id.action_helpSupportFragment_to_faqFragment)
        }

        // Navigate to ContactUsFragment when Contact Us card is clicked
        binding.contactUsCard.setOnClickListener {
            findNavController().navigate(R.id.action_helpSupportFragment_to_contactUsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}