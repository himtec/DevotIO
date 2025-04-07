package com.example.devotio.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.devotio.auth.GoogleSignInManager
import com.example.devotio.data.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import androidx.lifecycle.ViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val googleSignInManager = GoogleSignInManager(application)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _dailyReminderEnabled = MutableLiveData<Boolean>()
    val dailyReminderEnabled: LiveData<Boolean> = _dailyReminderEnabled

    private val _reminderTime = MutableLiveData<String>()
    val reminderTime: LiveData<String> = _reminderTime

    private val _text = MutableLiveData<String>().apply {
        value = "This is profile Fragment"
    }
    val text: LiveData<String> = _text

    init {
        loadUserData()
        loadPreferences()
    }

    private fun loadUserData() {
        // Simulated user data
        _user.value = User(
            id = 1,
            name = "John Doe",
            email = "john.doe@example.com",
            phoneNumber = "",
            profileImageUrl = "https://example.com/profile.jpg",
            favoriteTemples = emptyList(),
            favoritePrayers = emptyList(),
            dailyPrayerReminder = false,
            reminderTime = null
        )
    }

    private fun loadPreferences() {
        // TODO: Load preferences from SharedPreferences or database
        _dailyReminderEnabled.value = false
        _reminderTime.value = null
    }

    fun updateUserProfile(name: String, email: String) {
        _user.value = _user.value?.copy(
            name = name,
            email = email
        )
        // TODO: Update user data in repository
    }

    fun updateProfileImage(imageUrl: String) {
        val currentUser = _user.value
        if (currentUser != null) {
            _user.value = currentUser.copy(profileImageUrl = imageUrl)
            // TODO: Update profile image in backend
        }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        _dailyReminderEnabled.value = enabled
        // TODO: Save preference in SharedPreferences or database
    }

    fun updateReminderTime(time: String) {
        _reminderTime.value = time
        // TODO: Save preference in SharedPreferences or database
    }

    fun logout() {
        googleSignInManager.signOut()
        // TODO: Clear user data and preferences
    }
}

data class UserProfile(
    val name: String,
    val email: String
) 