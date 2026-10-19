package com.thinhnk.fastapp.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val authState: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
        return result.user ?: error("Không nhận được người dùng")
    }

    suspend fun signUpWithEmail(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val user = result.user ?: error("Không nhận được người dùng")
        user.sendEmailVerification()
        return user
    }

    suspend fun signInAnonymously(): FirebaseUser {
        val result = auth.signInAnonymously().await()
        return result.user ?: error("Không nhận được người dùng")
    }

    /** Đăng nhập Firebase bằng ID token đã lấy được từ bất kỳ luồng Google nào. */
    suspend fun signInWithGoogleIdToken(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return result.user ?: error("Không nhận được người dùng")
    }

    /** Liên kết Google với tài khoản email/mật khẩu đang đăng nhập. */
    suspend fun linkGoogleWithIdToken(idToken: String): FirebaseUser {
        val user = auth.currentUser ?: error("Chưa đăng nhập")
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = user.linkWithCredential(credential).await()
        return result.user ?: user
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email.trim()).await()
    }

    fun signOut() = auth.signOut()

    fun hasProvider(providerId: String): Boolean =
        auth.currentUser?.providerData?.any { it.providerId == providerId } == true

    companion object {
        const val PROVIDER_PASSWORD = "password"
        const val PROVIDER_GOOGLE = "google.com"
    }
}

/** Đổi mã lỗi Firebase thành câu tiếng Việt dễ hiểu. */
fun authErrorMessage(error: Throwable): String {
    if (error is FirebaseAuthUserCollisionException) {
        return "Email này đã có tài khoản bằng phương thức khác. " +
            "Hãy đăng nhập bằng mật khẩu rồi liên kết Google trong Hồ sơ."
    }

    val raw = error.message.orEmpty()
    return when {
        raw.contains("12501") ->
            "Đã huỷ đăng nhập Google."

        raw.contains("12500") ->
            "Google từ chối đăng nhập. Kiểm tra SHA-1 và Web client ID trong Firebase."

        raw.contains("no credential", ignoreCase = true) ->
            "Không tìm thấy tài khoản Google trên thiết bị. Hãy thêm tài khoản trong Cài đặt."

        raw.contains("password is invalid", ignoreCase = true) ||
            raw.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ->
            "Sai email hoặc mật khẩu."

        raw.contains("no user record", ignoreCase = true) ||
            raw.contains("USER_NOT_FOUND", ignoreCase = true) ->
            "Email này chưa có tài khoản."

        raw.contains("email address is already in use", ignoreCase = true) ||
            raw.contains("EMAIL_EXISTS", ignoreCase = true) ->
            "Email này đã được đăng ký. Hãy đăng nhập hoặc quên mật khẩu."

        raw.contains("network", ignoreCase = true) ->
            "Không có kết nối mạng. Kiểm tra lại rồi thử tiếp."

        raw.contains("badly formatted", ignoreCase = true) ->
            "Email không hợp lệ."

        raw.contains("at least 6 characters", ignoreCase = true) ->
            "Mật khẩu phải có ít nhất 6 ký tự."

        raw.contains("google_web_client_id", ignoreCase = true) ->
            "Chưa cấu hình Google Web client ID. Xem docs/HUONG-DAN-FIREBASE.md."

        else -> error.message ?: "Đăng nhập thất bại. Thử lại sau."
    }
}
