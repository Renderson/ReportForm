package com.rendersoncs.report.repository

import android.content.Context
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.User
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.login.util.User as FirebaseProfile
import kotlinx.coroutines.tasks.await

/**
 * Auth for the Compose login flow. Mirrors LoginActivity:
 * Firebase session is the source of uid; Room stores User(userId) for reports.
 */
class AuthRepository(
    private val reportRepository: ReportRepository,
    private val context: Context
) {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val currentUid: String?
        get() = firebaseAuth.currentUser?.uid

    fun isLoggedIn(): Boolean = firebaseAuth.currentUser != null

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .signInWithEmailAndPassword(email.trim(), password)
                .await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("User not found"))
            persistSession(firebaseUser)
            Result.success(firebaseUser)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    /**
     * Same as SignUpActivity.saveUser + AuthStateListener + verifyLogin:
     * createUser (already signs in), RTDB credential, Room uid.
     */
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        cargo: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth
                .createUserWithEmailAndPassword(email.trim(), password)
                .await()
            val firebaseUser = result.user
                ?: return Result.failure(IllegalStateException("User not found"))
            firebaseUser.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
            ).await()
            persistSession(
                firebaseUser = firebaseUser,
                name = name.trim(),
                cargo = cargo.trim()
            )
            Result.success(firebaseUser)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    /**
     * Same as LoginActivity.verifyLogin + AuthStateListener:
     * 1) Room: User(userId = uid)
     * 2) Prefs: empty provider (email/password)
     * 3) RTDB users/{uid}/credential when name/displayName exists (social)
     */
    suspend fun persistSession(
        firebaseUser: FirebaseUser,
        name: String? = null,
        cargo: String? = null
    ) {
        reportRepository.insertUser(User(userId = firebaseUser.uid))

        val profile = FirebaseProfile().apply {
            id = firebaseUser.uid
            if (name != null) {
                this.name = name
            } else {
                setNameIfNull(firebaseUser.displayName)
            }
            setEmailIfNull(firebaseUser.email)
            setUrlImgIfNull(firebaseUser.photoUrl)
            saveProviderSP(context, EMAIL_PROVIDER)
        }

        val hasName = profile.name != null || firebaseUser.displayName != null
        if (hasName || !cargo.isNullOrBlank()) {
            val payload = mutableMapOf<String, Any>(
                "id" to firebaseUser.uid
            )
            profile.name?.let { payload["name"] = it }
            profile.email?.let { payload["email"] = it }
            profile.photo?.toString()?.let { payload["photo"] = it }
            if (!cargo.isNullOrBlank()) {
                payload[CARGO] = cargo
            }
            LibraryClass.getFirebase()
                .child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(firebaseUser.uid)
                .child(ReportConstants.FIREBASE.FIRE_CREDENTIAL)
                .updateChildren(payload)
                .await()
        }
    }

    suspend fun fetchCredential(): Pair<String?, String?> {
        val uid = currentUid ?: return null to null
        return try {
            val snapshot = LibraryClass.getFirebase()
                .child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(uid)
                .child(ReportConstants.FIREBASE.FIRE_CREDENTIAL)
                .get()
                .await()
            val name = snapshot.child(ReportConstants.FIREBASE.FIRE_NAME).getValue(String::class.java)
            val photo = snapshot.child("photo").getValue(String::class.java)
                ?: snapshot.child(ReportConstants.FIREBASE.FIRE_PHOTO).getValue(String::class.java)
            name to photo
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            null to null
        }
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(IllegalStateException("User not found"))
        val email = user.email
            ?: return Result.failure(IllegalStateException("User not found"))
        return try {
            user.reauthenticate(EmailAuthProvider.getCredential(email, currentPassword)).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(password: String): Result<Unit> {
        val user = firebaseAuth.currentUser
            ?: return Result.failure(IllegalStateException("User not found"))
        val email = user.email
            ?: return Result.failure(IllegalStateException("User not found"))
        val uid = user.uid
        return try {
            user.reauthenticate(EmailAuthProvider.getCredential(email, password)).await()
            user.delete().await()
            LibraryClass.getFirebase()
                .child(ReportConstants.FIREBASE.FIRE_USERS)
                .child(uid)
                .setValue(null)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private companion object {
        const val EMAIL_PROVIDER = ""
        const val CARGO = "cargo"
    }
}
