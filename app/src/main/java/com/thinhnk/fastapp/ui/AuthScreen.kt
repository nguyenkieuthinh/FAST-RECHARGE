package com.thinhnk.fastapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thinhnk.fastapp.R
import com.thinhnk.fastapp.auth.AuthRepository
import com.thinhnk.fastapp.auth.authErrorMessage
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(auth: AuthRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var registerMode by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var googleBusy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(true) }

    val googleIdToken = rememberGoogleIdTokenProvider()

    fun submit(block: suspend () -> Unit) {
        if (loading) return
        loading = true
        message = null
        scope.launch {
            try {
                block()
            } catch (error: Throwable) {
                message = authErrorMessage(error)
                messageIsError = true
            } finally {
                loading = false
            }
        }
    }

    fun startGoogle() {
        if (loading) return
        loading = true
        googleBusy = true
        message = null
        scope.launch {
            try {
                val idToken = googleIdToken(context)
                auth.signInWithGoogleIdToken(idToken)
            } catch (cancel: GoogleSignInCancelled) {
                message = cancel.message
                messageIsError = true
            } catch (error: Throwable) {
                message = authErrorMessage(error)
                messageIsError = true
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
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "Fast",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            enabled = !loading,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            singleLine = true,
            enabled = !loading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = {
                if (registerMode) {
                    submit {
                        auth.signUpWithEmail(email, password)
                        message = "Đã tạo tài khoản. Kiểm tra email để xác minh."
                        messageIsError = false
                    }
                } else {
                    submit { auth.signInWithEmail(email, password) }
                }
            },
            enabled = !loading && email.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                if (registerMode) {
                    stringResource(R.string.create_account)
                } else {
                    stringResource(R.string.login_title)
                },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = { registerMode = !registerMode },
                enabled = !loading,
            ) {
                Text(
                    if (registerMode) {
                        "Đã có tài khoản? Đăng nhập"
                    } else {
                        stringResource(R.string.create_account)
                    },
                )
            }
            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        message = "Nhập email trước rồi bấm quên mật khẩu."
                        messageIsError = true
                    } else {
                        submit {
                            auth.sendPasswordReset(email)
                            message = "Đã gửi email đặt lại mật khẩu."
                            messageIsError = false
                        }
                    }
                },
                enabled = !loading,
            ) {
                Text(stringResource(R.string.forgot_password))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = "hoặc",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        OutlinedButton(
            onClick = { startGoogle() },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (googleBusy) "Đang mở Google…" else stringResource(R.string.continue_google))
        }

        TextButton(
            onClick = { submit { auth.signInAnonymously() } },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.continue_guest))
        }

        message?.let { text ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (messageIsError) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    },
                ),
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (messageIsError) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                )
            }
        }

        Text(
            text = stringResource(R.string.data_credit),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
