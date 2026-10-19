package com.thinhnk.fastapp.auth

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.thinhnk.fastapp.R
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

/**
 * Lấy ID token của Google qua Credential Manager.
 * Cần Web client ID (OAuth client loại Web application) trong strings.xml.
 *
 * Trên một số ROM (ví dụ MIUI/HyperOS), màn hình chọn tài khoản của
 * Credential Manager không mở được và lệnh gọi có thể treo vô hạn, nên luôn
 * đặt thời hạn. Khi lỗi/hết hạn, UI sẽ chuyển sang [legacySignInIntent].
 */
object GoogleSignInHelper {

    private const val CREDENTIAL_TIMEOUT_MS = 20_000L

    private fun serverClientId(context: Context): String {
        val id = context.getString(R.string.google_web_client_id)
        require(!id.startsWith("DAN_")) {
            "Chưa cấu hình google_web_client_id trong strings.xml"
        }
        return id
    }

    suspend fun getIdToken(context: Context): String {
        val credentialManager = CredentialManager.create(context)
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId(context))
                    .setAutoSelectEnabled(false)
                    .build(),
            )
            .build()

        val response = withTimeout(CREDENTIAL_TIMEOUT_MS) {
            credentialManager.getCredential(context, request)
        }
        val credential = response.credential

        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            error("Không nhận được Google ID token")
        }

        return GoogleIdTokenCredential.createFrom(credential.data).idToken
    }

    /**
     * Intent của Google Sign-In cũ (Play Services). Đây là luồng dự phòng:
     * màn hình chọn tài khoản do Play Services vẽ nên không phụ thuộc vào
     * Credential Manager của ROM.
     *
     * Đăng xuất khỏi Google trước khi lấy intent để xoá tài khoản đã ghi nhớ;
     * nếu không, lần đăng nhập sau sẽ lặng lẽ dùng lại đúng tài khoản cũ và
     * người dùng không có cách chọn tài khoản khác.
     */
    suspend fun legacySignInIntent(context: Context): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId(context))
            .requestEmail()
            .build()
        val client: GoogleSignInClient = GoogleSignIn.getClient(context, options)
        runCatching { client.signOut().await() }
        return client.signInIntent
    }

    /** Đọc ID token từ kết quả trả về của [legacySignInIntent]. */
    fun idTokenFromLegacyResult(data: Intent?): String {
        val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            .getResult(com.google.android.gms.common.api.ApiException::class.java)
        return account.idToken ?: error("Không nhận được Google ID token")
    }
}
