package com.group.FlyNest.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.group.FlyNest.R
import com.group.FlyNest.databinding.FragmentPrivacySettingsBinding

class PrivacySettingsFragment : Fragment() {

    private var _binding: FragmentPrivacySettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivacySettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.profileVisibilitySwitch.isChecked = loadProfileVisibility()
        binding.profileVisibilitySwitch.setOnCheckedChangeListener { _, isChecked ->
            saveProfileVisibility(isChecked)
            showSnackbar(view, "Profile Visibility ${if (isChecked) "enabled" else "disabled"}")
        }

        binding.blockedUsersCard.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_privacySettingsFragment_to_blockedUsersFragment)
            } catch (e: Exception) {
                showSnackbar(view, "Blocked Users screen not yet implemented")
            }
        }
    }

    private fun saveProfileVisibility(isVisible: Boolean) {
        val prefs = requireActivity().getSharedPreferences("privacy_settings", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("profile_visibility", isVisible).apply()
    }

    private fun loadProfileVisibility(): Boolean {
        val prefs = requireActivity().getSharedPreferences("privacy_settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("profile_visibility", true)
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}