package com.ozodbek.gymbuddy.data

import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.firestore
import com.ozodbek.gymbuddy.data.model.Workout
import com.ozodbek.gymbuddy.data.model.WorkoutInput
import com.ozodbek.gymbuddy.data.model.WorkoutType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreWorkoutRepository(
    private val db: FirebaseFirestore = Firebase.firestore,
    private val auth: FirebaseAuth = Firebase.auth
) : WorkoutRepository {

    private val workouts get() = db.collection("workouts")

    /** The owner id always comes from the signed-in session, never from the form. */
    private fun requireUid(): String =
        auth.currentUser?.uid ?: throw AppException("You are not signed in")

    override fun observeMyWorkouts(): Flow<List<Workout>> = callbackFlow {
        val registration = workouts
            .whereEqualTo("ownerId", requireUid()) // the query itself filters by owner
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(AppException(friendlyMessage(error), error))
                    return@addSnapshotListener
                }
                if (snapshot != null) trySend(snapshot.toObjects(Workout::class.java))
            }
        awaitClose { registration.remove() } // stop listening when the screen is gone
    }

    override suspend fun getWorkout(id: String): Result<Workout> = call {
        workouts.document(id).get().await().toObject(Workout::class.java)
            ?: throw AppException("This workout no longer exists")
    }

    override suspend fun getWorkoutTypes(): Result<List<WorkoutType>> = call {
        db.collection("workoutTypes").orderBy("sortOrder").get().await()
            .toObjects(WorkoutType::class.java)
    }

    override suspend fun addWorkout(input: WorkoutInput): Result<String> = call {
        val data = hashMapOf<String, Any>(
            "ownerId" to requireUid(),
            "title" to input.title,
            "typeId" to input.typeId,
            "typeName" to input.typeName,
            "startAt" to Timestamp(input.startAt),
            "endAt" to Timestamp(input.endAt),
            "notes" to input.notes,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        workouts.add(data).await().id
    }

    /** Changes the editable fields and updatedAt only; ownerId and createdAt are never sent. */
    override suspend fun updateWorkout(id: String, input: WorkoutInput): Result<Unit> = call {
        val changes = hashMapOf<String, Any>(
            "title" to input.title,
            "typeId" to input.typeId,
            "typeName" to input.typeName,
            "startAt" to Timestamp(input.startAt),
            "endAt" to Timestamp(input.endAt),
            "notes" to input.notes,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        workouts.document(id).update(changes).await()
        Unit
    }

    override suspend fun deleteWorkout(id: String): Result<Unit> = call {
        workouts.document(id).delete().await()
        Unit
    }

    private suspend fun <T> call(block: suspend () -> T): Result<T> =
        try {
            Result.success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(AppException(friendlyMessage(e), e))
        }

    private fun friendlyMessage(e: Throwable): String =
        when ((e as? FirebaseFirestoreException)?.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED -> "You do not have access to this data"
            FirebaseFirestoreException.Code.UNAVAILABLE -> "No connection. Check your internet and try again"
            FirebaseFirestoreException.Code.NOT_FOUND -> "This workout no longer exists"
            else -> "Something went wrong. Please try again"
        }
}
