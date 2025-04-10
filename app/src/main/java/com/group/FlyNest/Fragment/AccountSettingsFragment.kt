package com.group.FlyNest.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.group.FlyNest.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout

class AccountSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Toolbar setup
        val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Change Password setup
        val currentPasswordLayout = view.findViewById<TextInputLayout>(R.id.currentPasswordLayout)
        val newPasswordLayout = view.findViewById<TextInputLayout>(R.id.newPasswordLayout)
        val changePasswordButton = view.findViewById<MaterialButton>(R.id.changePasswordButton)

        changePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordLayout.editText?.text.toString()
            val newPassword = newPasswordLayout.editText?.text.toString()

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                showSnackbar(view, "Please fill in all password fields")
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                showSnackbar(view, "Password must be at least 6 characters")
                return@setOnClickListener
            }

            // TODO: Implement actual password change logic with your backend
            showSnackbar(view, "Password updated successfully")
            currentPasswordLayout.editText?.text?.clear()
            newPasswordLayout.editText?.text?.clear()
        }

        // Update Email setup
        val emailLayout = view.findViewById<TextInputLayout>(R.id.emailLayout)
        val updateEmailButton = view.findViewById<MaterialButton>(R.id.updateEmailButton)

        updateEmailButton.setOnClickListener {
            val newEmail = emailLayout.editText?.text.toString()

            if (newEmail.isEmpty()) {
                showSnackbar(view, "Please enter an email")
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                showSnackbar(view, "Please enter a valid email")
                return@setOnClickListener
            }

            // TODO: Implement actual email update logic with your backend
            showSnackbar(view, "Email updated successfully")
            emailLayout.editText?.text?.clear()
        }

        // Delete Account setup
        val deleteAccountButton = view.findViewById<MaterialButton>(R.id.deleteAccountButton)

        deleteAccountButton.setOnClickListener {
            // TODO: Consider adding a confirmation dialog here
            // TODO: Implement actual account deletion logic with your backend
            showSnackbar(view, "Account deletion requested")
        }
    }

    private fun showSnackbar(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
    }
}