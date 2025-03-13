package com.group.FlyNest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.group.FlyNest.databinding.ActivitySignUpBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this)

        // Check Google Play Services
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            Toast.makeText(this, "Google Play Services is not available", Toast.LENGTH_LONG).show()
            Log.e("SignUpActivity", "Google Play Services unavailable, resultCode: $resultCode")
            return
        }

        // Check if user is already signed in
        if (auth.currentUser != null) {
            Log.d("SignUpActivity", "User already signed in: ${auth.currentUser?.email}")
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Toggle between Email and Phone Sign-Up
        binding.emailButton.setOnClickListener {
            binding.viewSwitcher.displayedChild = 0 // Show Email Sign-Up View
            binding.emailButton.setTextColor(resources.getColor(R.color.primary_color)) // Use your primary color
            binding.phoneButton.setTextColor(resources.getColor(R.color.grey)) // Use your grey color
        }

        binding.phoneButton.setOnClickListener {
            binding.viewSwitcher.displayedChild = 1 // Show Phone Sign-Up View
            binding.phoneButton.setTextColor(resources.getColor(R.color.primary_color)) // Use your primary color
            binding.emailButton.setTextColor(resources.getColor(R.color.grey)) // Use your grey color
        }

        // Email/Password Sign-Up
        binding.SignUp.setOnClickListener {
            if (binding.viewSwitcher.displayedChild == 0) {
                // Email Sign-Up
                val name = binding.userName.text.toString().trim()
                val email = binding.emailaddres.text.toString().trim()
                val password = binding.passwordin.text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (!isValidEmail(email)) {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                } else {
                    handleEmailSignUp(name, email, password)
                }
            } else {
                // Phone Sign-Up
                val name = binding.phoneName.text.toString().trim()
                val phoneNumber = binding.phoneNumber.text.toString().trim()
                val password = binding.phonePassword.text.toString().trim()

                if (name.isEmpty() || phoneNumber.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show()
                } else {
                    handlePhoneSignUp(name, phoneNumber, password)
                }
            }
        }

        // Google Sign-Up
        binding.googleSignUpButton.setOnClickListener {
            signUpWithGoogle()
        }

        // Redirect to Login
        binding.SignIn.setOnClickListener {
            Log.d("SignUpActivity", "Redirecting to LoginActivity")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun handleEmailSignUp(name: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
        binding.SignUp.isEnabled = false

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                binding.overlayView.visibility = View.GONE
                binding.SignUp.isEnabled = true

                if (task.isSuccessful) {
                    Log.d("SignUpActivity", "Sign-up successful for $email")
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    // Save user data to Firestore
                    val user: Map<String, Any> = mapOf(
                        "name" to name,
                        "email" to email,
                        "phoneNumber" to "" // No phone number for email signup
                    )
                    saveUserToFirestore(user)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("SignUpActivity", "Sign-up failed", task.exception)
                    val errorMessage = when (task.exception?.message) {
                        "The email address is already in use by another account." ->
                            "Email already in use. Please use a different email."
                        "The email address is badly formatted." ->
                            "Invalid email format. Please check your email."
                        else -> "Registration failed: ${task.exception?.message ?: "Unknown error"}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun handlePhoneSignUp(name: String, phoneNumber: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
        binding.SignUp.isEnabled = false

        // Create a user with email/password (using a dummy email)
        val dummyEmail = "$phoneNumber@flynest.com" // Use phone number as part of the email
        auth.createUserWithEmailAndPassword(dummyEmail, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                binding.overlayView.visibility = View.GONE
                binding.SignUp.isEnabled = true

                if (task.isSuccessful) {
                    Log.d("SignUpActivity", "Phone sign-up successful for $phoneNumber")
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    // Save user data to Firestore
                    val user: Map<String, Any> = mapOf(
                        "name" to name,
                        "email" to dummyEmail,
                        "phoneNumber" to phoneNumber
                    )
                    saveUserToFirestore(user)

                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("SignUpActivity", "Phone sign-up failed", task.exception)
                    val errorMessage = when (task.exception?.message) {
                        "The email address is already in use by another account." ->
                            "Phone number already in use. Please use a different phone number."
                        else -> "Registration failed: ${task.exception?.message ?: "Unknown error"}"
                    }
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun saveUserToFirestore(user: Map<String, Any>) {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("SignUpActivity", "User data saved to Firestore")
            }
            .addOnFailureListener { e ->
                Log.e("SignUpActivity", "Failed to save user data to Firestore", e)
                Toast.makeText(this, "Failed to save user data. Please try again.", Toast.LENGTH_LONG).show()
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun signUpWithGoogle() {
        binding.progressBar.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .build()

        Log.d("SignUpActivity", "Launching Google Sign-In with client ID: ${getString(R.string.default_web_client_id)}")

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = credentialManager.getCredential(this@SignUpActivity, request)
                val credential = result.credential
                Log.d("SignUpActivity", "Credential received: ${credential.javaClass.simpleName}")

                val googleIdToken = when (credential) {
                    is GoogleIdTokenCredential -> {
                        credential.idToken
                    }
                    is CustomCredential -> {
                        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                            googleCredential.idToken
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@SignUpActivity, "Unsupported custom credential type: ${credential.type}", Toast.LENGTH_LONG).show()
                            }
                            return@launch
                        }
                    }
                    else -> {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SignUpActivity, "Unexpected credential type: ${credential.javaClass.simpleName}", Toast.LENGTH_LONG).show()
                        }
                        return@launch
                    }
                }

                Log.d("SignUpActivity", "Google ID Token: $googleIdToken")
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.overlayView.visibility = View.GONE

                    if (authResult.user != null) {
                        Log.d("SignUpActivity", "Google Sign-Up successful: ${authResult.user?.email}")
                        Toast.makeText(this@SignUpActivity, "Google Sign-Up successful", Toast.LENGTH_SHORT).show()

                        // Save user data to Firestore
                        val user: Map<String, Any> = mapOf(
                            "name" to (authResult.user?.displayName ?: ""),
                            "email" to (authResult.user?.email ?: ""),
                            "phoneNumber" to "" // No phone number for Google signup
                        )
                        saveUserToFirestore(user)

                        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Log.e("SignUpActivity", "Google Sign-Up failed: No user returned")
                        Toast.makeText(this@SignUpActivity, "Google Sign-Up failed", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: GetCredentialException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.overlayView.visibility = View.GONE
                    Log.e("SignUpActivity", "Credential Manager error: ${e.type}", e)
                    Toast.makeText(this@SignUpActivity, "Google Sign-Up failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.overlayView.visibility = View.GONE
                    Log.e("SignUpActivity", "Unexpected error during Google Sign-Up", e)
                    Toast.makeText(this@SignUpActivity, "Unexpected error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}