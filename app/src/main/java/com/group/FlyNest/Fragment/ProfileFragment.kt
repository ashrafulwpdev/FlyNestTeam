package com.group.FlyNest.Fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.group.FlyNest.LoginActivity
import com.group.FlyNest.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var profilePhotoImageView: CircleImageView
    private lateinit var userNameTextView: TextView
    private lateinit var logOutButton: Button
    private lateinit var editNameButton: Button

    private val REQUEST_CODE_PICK_IMAGE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Initialize views
        profilePhotoImageView = view.findViewById(R.id.profilePhotoImageView)
        userNameTextView = view.findViewById(R.id.userNameTextView)
        logOutButton = view.findViewById(R.id.logOutButton)
        editNameButton = view.findViewById(R.id.editNameIcon)

        // Check if views are properly initialized
        if (profilePhotoImageView == null || userNameTextView == null || logOutButton == null || editNameButton == null) {
            Toast.makeText(requireContext(), "Error: Views not initialized", Toast.LENGTH_SHORT).show()
            return view
        }

        // Load saved profile photo and name
        loadUserProfile()

        // Set click listeners
        profilePhotoImageView.setOnClickListener {
            openGalleryForImage()
        }

        logOutButton.setOnClickListener {
            logOutUser()
        }

        editNameButton.setOnClickListener {
            showEditNameDialog()
        }

        return view
    }

    private fun loadUserProfile() {
        // Load saved profile photo
        val sharedPreferences = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE)
        val profilePhotoUri = sharedPreferences.getString("profile_photo_uri", null)
        if (profilePhotoUri != null) {
            Glide.with(requireContext())
                .load(Uri.parse(profilePhotoUri))
                .into(profilePhotoImageView)
        }

        // Load saved user name
        val userName = sharedPreferences.getString("user_name", "John Doe")
        userNameTextView.text = userName
    }

    private fun openGalleryForImage() {
        // Check for permissions
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
        } else {
            // Request permissions
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PICK_IMAGE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == android.app.Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                // Log the selected image URI
                Log.d("ProfileFragment", "Selected Image URI: $selectedImageUri")

                // Load the selected image into the CircleImageView
                Glide.with(requireContext())
                    .load(selectedImageUri)
                    .into(profilePhotoImageView)

                // Save the image URI to SharedPreferences
                saveProfilePhotoUri(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveProfilePhotoUri(uri: Uri?) {
        val sharedPreferences = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("profile_photo_uri", uri.toString())
        editor.apply()
    }

    private fun logOutUser() {
        // Clear user session (e.g., clear SharedPreferences, Firebase Auth, etc.)
        val sharedPreferences = requireContext().getSharedPreferences("user_session", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        // Navigate to the login screen
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // Close the current activity
    }

    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_name, null)
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Name")
            .create()

        saveButton.setOnClickListener {
            val newName = editTextName.text.toString()
            if (newName.isNotEmpty()) {
                // Update the name in the TextView
                userNameTextView.text = newName

                // Save the new name to SharedPreferences
                saveUserName(newName)

                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun saveUserName(name: String) {
        val sharedPreferences = requireContext().getSharedPreferences("user_profile", android.content.Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("user_name", name)
        editor.apply()
    }
}