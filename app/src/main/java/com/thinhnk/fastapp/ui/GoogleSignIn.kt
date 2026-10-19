package com.thinhnk.fastapp.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.thinhnk.fastapp.auth.GoogleSignInHelper
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Người dùng tự đóng bảng chọn tài khoản Google. */
class GoogleSignInCancelled : Exception("Đã huỷ đăng nhập Google.")

/**
 * Trả về hàm lấy Google ID token.
 *
 * Ưu tiên Google Sign-In cũ của Play Services: bảng chọn tài khoản mở nhanh và
 * không phụ thuộc vào màn hình chọn tài khoản của ROM. Trên MIUI/HyperOS màn
 * hình đó có thể không mở được, khiến Credential Manager treo rất lâu.
 * Chỉ khi luồng cũ không dùng được mới thử Credential Manager.
 */
@Composable
fun rememberGoogleIdTokenProvider(): suspend (Context) -> String {
    val holder = remember { GoogleIdTokenHolder() }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        holder::onResult,
    )
    holder.launch = { intent -> launcher.launch(intent) }

    return holder::getToken
}

private class GoogleIdTokenHolder {

    var launch: (Intent) -> Unit = {}

    private var pending: CompletableDeferred<String>? = null

    fun onResult(result: ActivityResult) {
        val deferred = pending ?: return
        pending = null

        val data = result.data
        if (result.resultCode != Activity.RESULT_OK || data == null) {
            deferred.completeExceptionally(GoogleSignInCancelled())
            return
        }

        runCatching { GoogleSignInHelper.idTokenFromLegacyResult(data) }
            .onSuccess { deferred.complete(it) }
            .onFailure { error ->
                val isCancelled = error is ApiException &&
                    error.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED
                deferred.completeExceptionally(
                    if (isCancelled) GoogleSignInCancelled() else error,
                )
            }
    }

    suspend fun getToken(context: Context): String {
        val deferred = CompletableDeferred<String>()
        pending = deferred

        val launched = runCatching {
            withContext(Dispatchers.Main) {
                launch(GoogleSignInHelper.legacySignInIntent(context))
            }
        }
        if (launched.isFailure) {
            pending = null
            return credentialManagerToken(context)
        }

        return try {
            deferred.await()
        } catch (cancel: GoogleSignInCancelled) {
            throw cancel
        } catch (legacyError: Throwable) {
            // Máy không hỗ trợ luồng cũ (GMS thiếu/cũ) -> thử Credential Manager.
            credentialManagerToken(context)
        }
    }
}

private suspend fun credentialManagerToken(context: Context): String =
    try {
        GoogleSignInHelper.getIdToken(context)
    } catch (cancel: GetCredentialCancellationException) {
        throw GoogleSignInCancelled()
    }
