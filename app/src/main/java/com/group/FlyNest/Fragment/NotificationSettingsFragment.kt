package com.group.FlyNest.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.group.FlyNest.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class NotificationSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        val pushSwitch = view.findViewById<SwitchMaterial>(R.id.pushSwitch)
        val emailSwitch = view.findViewById<SwitchMaterial>(R.id.emailSwitch)

        pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            showSnackbar(view, "Push Notifications ${if (isChecked) "enabled" else "disabled"}")
        }

        emailSwitch.setOnCheckedChangeListener { _, isChecked ->
            showSnackbar(view, "Email Notifications ${if (isChecked) "enabled" else "disabled"}")
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }
}