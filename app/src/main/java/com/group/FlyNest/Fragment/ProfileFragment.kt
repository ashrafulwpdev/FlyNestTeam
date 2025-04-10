package com.group.FlyNest.Fragment

<<<<<<< HEAD
import android.app.AlertDialog
=======
import android.Manifest
import android.app.AlertDialog
import android.content.Context
>>>>>>> upstream/main
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
<<<<<<< HEAD
=======
import android.provider.Settings
>>>>>>> upstream/main
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
<<<<<<< HEAD
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
=======
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
>>>>>>> upstream/main
import com.group.FlyNest.LoginActivity
import com.group.FlyNest.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

<<<<<<< HEAD
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
=======
    // UI Components
    private lateinit var profileImage: CircleImageView
    private lateinit var profileName: TextView
    private lateinit var profileTag: TextView
    private lateinit var profileBio: TextView
    private lateinit var editProfileButton: FloatingActionButton
    private lateinit var editBioButton: MaterialButton
    private lateinit var logoutButton: TextView
    private lateinit var tripsCount: TextView
    private lateinit var countriesCount: TextView
    private lateinit var ratingCount: TextView
    private lateinit var accountSettings: TextView
    private lateinit var notificationSettings: TextView
    private lateinit var privacySettings: TextView
    private lateinit var helpSupport: TextView

    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    // Activity Result API for gallery and permissions
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { handleSelectedImage(it) } ?: run {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        when {
            isGranted -> {
                openGallery()
            }
            !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), STORAGE_PERMISSION) -> {
                showPermissionPermanentlyDeniedDialog()
            }
            else -> {
                showPermissionDeniedDialog()
>>>>>>> upstream/main
            }
        }
    }

<<<<<<< HEAD
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
=======
    // Constants
    companion object {
        private const val TAG = "ProfileFragment"
        private const val PREFS_NAME = "user_profile"
        private const val KEY_PHOTO_URI = "profile_photo_uri"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_BIO = "user_bio"
        private const val KEY_TRIPS = "trips_count"
        private const val KEY_COUNTRIES = "countries_count"
        private const val KEY_RATING = "rating_count"
        private const val SESSION_PREFS = "user_session"
        private const val STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews(view)
        setupClickListeners()
        loadUserProfile()
    }

    private fun initializeViews(view: View) {
        profileImage = view.findViewById(R.id.profile_image) ?: throw IllegalStateException("Profile image not found")
        profileName = view.findViewById(R.id.profile_name) ?: throw IllegalStateException("Profile name not found")
        profileTag = view.findViewById(R.id.profile_tag) ?: throw IllegalStateException("Profile tag not found")
        profileBio = view.findViewById(R.id.profile_bio) ?: throw IllegalStateException("Profile bio not found")
        editProfileButton = view.findViewById(R.id.edit_profile_button) ?: throw IllegalStateException("Edit profile button not found")
        editBioButton = view.findViewById(R.id.edit_bio_button) ?: throw IllegalStateException("Edit bio button not found")
        logoutButton = view.findViewById(R.id.logout_button) ?: throw IllegalStateException("Logout button not found")
        tripsCount = view.findViewById(R.id.trips_count) ?: throw IllegalStateException("Trips count not found")
        countriesCount = view.findViewById(R.id.countries_count) ?: throw IllegalStateException("Countries count not found")
        ratingCount = view.findViewById(R.id.rating_count) ?: throw IllegalStateException("Rating count not found")
        accountSettings = view.findViewById(R.id.account_settings) ?: throw IllegalStateException("Account settings not found")
        notificationSettings = view.findViewById(R.id.notification_settings) ?: throw IllegalStateException("Notification settings not found")
        privacySettings = view.findViewById(R.id.privacy_settings) ?: throw IllegalStateException("Privacy settings not found")
        helpSupport = view.findViewById(R.id.help_support) ?: throw IllegalStateException("Help and support not found")
    }

    private fun setupClickListeners() {
        profileImage.setOnClickListener {
            checkAndRequestStoragePermission()
        }

        editProfileButton.setOnClickListener {
            showEditNameDialog()
        }
        editProfileButton.setImageResource(R.drawable.ic_edit)
        editProfileButton.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.white)
        editProfileButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary_color)

        editBioButton.setOnClickListener {
            showEditBioDialog()
        }

        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        accountSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_accountSettingsFragment)
        }
        notificationSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationSettingsFragment)
        }
        privacySettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_privacySettingsFragment)
        }
        helpSupport.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_helpSupportFragment)
        }
    }

    private fun loadUserProfile() {
        try {
            val sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

            sharedPreferences.getString(KEY_PHOTO_URI, null)?.let { uriString ->
                Glide.with(this)
                    .load(Uri.parse(uriString))
                    .error(R.drawable.default_profile)
                    .fallback(R.drawable.default_profile)
                    .circleCrop()
                    .into(profileImage)
            } ?: run {
                profileImage.setImageResource(R.drawable.default_profile)
            }

            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                profileName.text = firebaseUser.displayName ?: sharedPreferences.getString(KEY_USER_NAME, "Alex Johnson")
            } else {
                profileName.text = sharedPreferences.getString(KEY_USER_NAME, "Alex Johnson")
            }
            profileTag.text = "@${profileName.text}"
            profileBio.text = sharedPreferences.getString(KEY_USER_BIO, "Digital nomad and travel enthusiast. Exploring the world one country at a time. ✈️🌍")
            tripsCount.text = sharedPreferences.getInt(KEY_TRIPS, 128).toString()
            countriesCount.text = sharedPreferences.getInt(KEY_COUNTRIES, 24).toString()
            ratingCount.text = String.format("%.1f", sharedPreferences.getFloat(KEY_RATING, 5.0f))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load profile", e)
            Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndRequestStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                STORAGE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            shouldShowPermissionRationale() -> {
                showPermissionRationaleDialog()
            }
            else -> {
                requestStoragePermission()
            }
        }
    }

    private fun shouldShowPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            STORAGE_PERMISSION
        ) || !requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean("permission_rationale_shown", false)
    }

    private fun requestStoragePermission() {
        try {
            requestPermissionLauncher.launch(STORAGE_PERMISSION)
        } catch (e: Exception) {
            Log.e(TAG, "Error requesting permission", e)
            Toast.makeText(context, "Error requesting permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun handleSelectedImage(uri: Uri) {
        try {
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(profileImage)
            saveProfilePhotoUri(uri)
            Toast.makeText(context, "Profile photo updated", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading image", e)
            Toast.makeText(context, "Error updating profile photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfilePhotoUri(uri: Uri) {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PHOTO_URI, uri.toString())
            .apply()
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Storage Access Needed")
            .setMessage("We need storage access to let you choose a profile photo from your gallery. This permission is essential for updating your profile picture.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("permission_rationale_shown", true)
                    .apply()
                requestStoragePermission()
            }
            .setNegativeButton("Not Now") { _, _ ->
                Toast.makeText(context, "Profile photo update unavailable without permission", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("Storage access is necessary to update your profile photo. You can enable it in Settings or try again.")
            .setPositiveButton("Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Try Again") { _, _ ->
                checkAndRequestStoragePermission()
            }
            .setNeutralButton("Cancel") { _, _ ->
                Toast.makeText(context, "Profile photo update cancelled", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun showPermissionPermanentlyDeniedDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Permission Blocked")
            .setMessage("Storage permission has been permanently denied. Please enable it in app settings to update your profile photo.")
            .setPositiveButton("Go to Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel") { _, _ ->
                Toast.makeText(context, "Profile photo update unavailable", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        auth.signOut()
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        requireContext().getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
>>>>>>> upstream/main
    }

    private fun showEditNameDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_name, null)
<<<<<<< HEAD
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
=======
        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName).apply {
            setText(profileName.text)
            requestFocus()
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Name")
            .setPositiveButton("Save") { _, _ ->
                val newName = editTextName.text.toString().trim()
                when {
                    newName.isEmpty() -> Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    newName.length > 50 -> Toast.makeText(requireContext(), "Name too long", Toast.LENGTH_SHORT).show()
                    else -> {
                        profileName.text = newName
                        profileTag.text = "@$newName"
                        saveUserName(newName)
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditBioDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_bio, null)
        val editTextBio = dialogView.findViewById<EditText>(R.id.editTextBio).apply {
            setText(profileBio.text)
            requestFocus()
        }

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Edit Bio")
            .setPositiveButton("Save") { _, _ ->
                val newBio = editTextBio.text.toString().trim()
                if (newBio.length <= 150) {
                    profileBio.text = newBio
                    saveUserBio(newBio)
                } else {
                    Toast.makeText(requireContext(), "Bio too long (max 150 chars)", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveUserName(name: String) {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    private fun saveUserBio(bio: String) {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_USER_BIO, bio)
            .apply()
>>>>>>> upstream/main
    }
}