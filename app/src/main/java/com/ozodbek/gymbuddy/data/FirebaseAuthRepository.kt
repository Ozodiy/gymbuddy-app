package com.ozodbek.gymbuddy.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = Firebase.auth
) : AuthRepository {

    override val currentEmail: String? get() = auth.currentUser?.email
    override val isLoggedIn: Boolean get() = auth.currentUser != null

    override fun observeLoggedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun register(email: String, password: String): Result<Unit> =
        runCatching { auth.createUserWithEmailAndPassword(email, password).await(); Unit }

    override suspend fun login(email: String, password: String): Result<Unit> =
        runCatching { auth.signInWithEmailAndPassword(email, password).await(); Unit }

    override fun logout() = auth.signOut()
}
