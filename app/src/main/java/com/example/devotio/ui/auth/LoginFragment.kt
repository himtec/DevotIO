package com.example.devotio.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.devotio.R
import com.example.devotio.auth.GoogleSignInManager
import com.example.devotio.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.Status

class LoginFragment : Fragment() {
    private val TAG = "LoginFragment"
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleSignInManager: GoogleSignInManager
    private var progressAnimation: android.view.animation.Animation? = null

    private val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(TAG, "Sign in result code: ${result.resultCode}")
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            showLoading(true)
            val account = googleSignInManager.handleSignInResult(result.data)
            if (account != null) {
                Log.d(TAG, "Sign in successful, navigating to dashboard")
                findNavController().navigate(R.id.action_login_to_dashboard)
            } else {
                showLoading(false)
                handleSignInError(null)
            }
        } else {
            showLoading(false)
            val status = result.data?.getParcelableExtra("status") as? Status
            Log.e(TAG, "Sign in failed with result code: ${result.resultCode}, status: ${status?.statusCode}")
            handleSignInError(status)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        googleSignInManager = GoogleSignInManager(requireContext())
        setupClickListeners()

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_left)
        progressAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)

        // Apply animations to views
        binding.appIcon.startAnimation(fadeIn)
        binding.appName.startAnimation(slideUp)
        binding.welcomeMessage.startAnimation(slideUp)
        binding.googleSignInButton.startAnimation(slideUp)
    }

    private fun setupClickListeners() {
        binding.googleSignInButton.setOnClickListener {
            if (!googleSignInManager.isGooglePlayServicesAvailable()) {
                Snackbar.make(binding.root, "Google Play Services is not available", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            try {
                showLoading(true)
                val signInIntent = googleSignInManager.getSignInIntent()
                Log.d(TAG, "Launching sign in intent")
                signInLauncher.launch(signInIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Error launching sign in intent", e)
                showLoading(false)
                Snackbar.make(binding.root, "Error starting sign in process", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressContainer.visibility = if (show) View.VISIBLE else View.GONE
        binding.googleSignInButton.isEnabled = !show
        
        if (show) {
            binding.progressImage.startAnimation(progressAnimation)
        } else {
            binding.progressImage.clearAnimation()
        }
    }

    private fun handleSignInError(status: Status?) {
        val statusCode = status?.statusCode ?: GoogleSignInStatusCodes.SIGN_IN_FAILED
        val errorMessage = when (statusCode) {
            GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in was cancelled"
            GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Sign in is already in progress"
            GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign in failed. Please try again."
            GoogleSignInStatusCodes.NETWORK_ERROR -> "Network error occurred. Please check your internet connection."
            GoogleSignInStatusCodes.INVALID_ACCOUNT -> "Invalid account. Please try again."
            GoogleSignInStatusCodes.TIMEOUT -> "Sign in timed out. Please try again."
            else -> "An error occurred during sign in (Error code: $statusCode)"
        }
        
        Log.e(TAG, "Sign in error: $errorMessage")
        Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG)
            .setAction("Retry") {
                showLoading(true)
                signInLauncher.launch(googleSignInManager.getSignInIntent())
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressAnimation?.cancel()
        _binding = null
    }
} 