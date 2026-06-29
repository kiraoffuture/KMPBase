package com.kira.kmpbase.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.kira.kmpbase.core.ui.generated.resources.Res
import com.kira.kmpbase.core.ui.generated.resources.auth_email
import com.kira.kmpbase.core.ui.generated.resources.auth_password
import com.kira.kmpbase.core.ui.generated.resources.auth_sign_in
import com.kira.kmpbase.core.ui.localization.toLocalizedMessage
import com.kira.kmpbase.core.ui.viewmodel.koinViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val errorMessage = uiState.error?.toLocalizedMessage()
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val submitLogin = {
        if (!uiState.isLoading) {
            focusManager.clearFocus()
            viewModel.login(email, password)
        }
    }
    val focusPassword = {
        passwordFocusRequester.requestFocus()
    }

    LaunchedEffect(uiState.token) {
        if (!uiState.token.isNullOrBlank()) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .onPreviewKeyEvent { event ->
                        if (
                            event.type == KeyEventType.KeyDown &&
                            (event.key == Key.Tab || event.key == Key.Enter)
                        ) {
                            focusPassword()
                            true
                        } else {
                            false
                        }
                    },
                label = { Text(stringResource(Res.string.auth_email)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusPassword() },
                    onDone = { focusPassword() },
                ),
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .focusRequester(passwordFocusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Enter) {
                            submitLogin()
                            true
                        } else {
                            false
                        }
                    },
                label = { Text(stringResource(Res.string.auth_password)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitLogin() },
                ),
            )
            LoginFormErrorSlot(
                message = errorMessage,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            Button(
                onClick = submitLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            ) {
                Text(stringResource(Res.string.auth_sign_in))
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
private fun LoginFormErrorSlot(
    message: String?,
    modifier: Modifier = Modifier,
) {
    val typography = MaterialTheme.typography.bodyMedium
    val slotHeight = with(LocalDensity.current) {
        (typography.lineHeight.value * 2).dp
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(slotHeight),
        contentAlignment = Alignment.TopStart,
    ) {
        if (!message.isNullOrBlank()) {
            Text(
                text = message,
                style = typography,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}
