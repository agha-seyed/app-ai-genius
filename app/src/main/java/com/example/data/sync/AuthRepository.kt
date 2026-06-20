package com.example.data.sync

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun mockSignIn() {
        _currentUser.value = User("user_123", "کاربر تستی", "test@example.com")
    }

    fun signOut() {
        _currentUser.value = null
    }
}

data class User(
    val uid: String,
    val displayName: String,
    val email: String
)
