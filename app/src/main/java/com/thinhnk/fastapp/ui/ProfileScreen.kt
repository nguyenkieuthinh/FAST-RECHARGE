package com.thinhnk.fastapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import com.thinhnk.fastapp.FastApp
import com.thinhnk.fastapp.R
import com.thinhnk.fastapp.auth.AuthRepository
import com.thinhnk.fastapp.auth.authErrorMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ProfileScreen(app: FastApp, user: FirebaseUser) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = app.authRepository

    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var googleBusy by remember { mutableStateOf(false) }

    val googleIdToken = rememberGoogleIdTokenProvider()

    val isGuest = user.isAnonymous
    val hasGoogle = auth.hasProvider(AuthRepository.PROVIDER_GOOGLE)
    val hasPassword = auth.hasProvider(AuthRepository.PROVIDER_PASSWORD)

    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty()
    }

    fun run(block: suspend () -> String?) {
        if (loading) return
        loading = true
        message = null
        scope.launch {
            try {
                message = block()
            } catch (error: Throwable) {
                message = authErrorMessage(error)
            } finally {
                loading = false
            }
        }
    }

    fun startGoogle(link: Boolean) {
        if (loading) return
        loading = true
        googleBusy = true
        message = null
        scope.launch {
            try {
                val idToken = googleIdToken(context)
                if (link) {
                    auth.linkGoogleWithIdToken(idToken)
                    message = "Đã liên kết Google. Lần sau có thể đăng nhập bằng Google."
                } else {
                    auth.signInWithGoogleIdToken(idToken)
                }
            } catch (cancel: GoogleSignInCancelled) {
                message = cancel.message
            } catch (error: Throwable) {
                message = authErrorMessage(error)
            } finally {
                googleBusy = false
                loading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            text = stringResource(R.string.tab_profile),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = when {
                        !user.displayName.isNullOrBlank() -> user.displayName!!
                        !user.email.isNullOrBlank() -> user.email!!
                        isGuest -> "Khách"
                        else -> "Người dùng"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )

                user.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (!user.isEmailVerified && hasPassword) {
                        Text(
                            text = "Email chưa xác minh. Kiểm tra hộp thư (cả mục Spam).",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                val providers = buildList {
                    if (hasGoogle) add("Google")
                    if (hasPassword) add("Email / mật khẩu")
                    if (isGuest) add("Khách")
                }
                if (providers.isNotEmpty()) {
                    Text(
                        text = "Phương thức: ${providers.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Text(
                    text = "UID: ${user.uid}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Lưu trạm trên thiết bị",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    AssistChip(
                        onClick = { },
                        label = { Text("Đang phát triển") },
                    )
                }
                Text(
                    text = "Danh sách hiện lưu cục bộ bằng Room. " +
                        "Đồng bộ nhiều thiết bị sẽ được bật khi có cách xử lý xung đột rõ ràng.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        if (hasPassword && !hasGoogle) {
            Button(
                onClick = { startGoogle(link = true) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (googleBusy) "Đang mở Google…" else "Liên kết tài khoản Google")
            }
        }

        if (hasPassword && !user.isEmailVerified) {
            OutlinedButton(
                onClick = {
                    run {
                        user.sendEmailVerification().await()
                        "Đã gửi lại email xác minh tới ${user.email.orEmpty()}."
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Gửi lại email xác minh")
            }
        }

        if (isGuest) {
            OutlinedButton(
                onClick = { startGoogle(link = false) },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (googleBusy) "Đang mở Google…" else stringResource(R.string.continue_google))
            }
        }

        message?.let { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        HorizontalDivider()

        OutlinedButton(
            onClick = { auth.signOut() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (isGuest) "Thoát chế độ khách" else "Đăng xuất")
        }

        if (!isGuest) {
            Text(
                text = "Mẹo: đăng nhập lại bằng đúng phương thức đã dùng để giữ cùng một tài khoản.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = stringResource(R.string.data_credit),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (versionName.isNotBlank()) {
            Text(
                text = "Phiên bản $versionName",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
