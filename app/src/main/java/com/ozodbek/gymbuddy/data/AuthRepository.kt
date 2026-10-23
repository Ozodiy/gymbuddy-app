package com.ozodbek.gymbuddy.data

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentEmail: String?
    val isLoggedIn: Boolean
    fun observeLoggedIn(): Flow<Boolean>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun login(email: String, password: String): Result<Unit>
    fun logout()
}
