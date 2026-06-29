package com.kira.kmpbase.core.ui.localization

import androidx.compose.runtime.Composable
import com.kira.kmpbase.core.domain.model.AppError
import com.kira.kmpbase.core.domain.model.ValidationErrorCode
import com.kira.kmpbase.core.domain.validation.LoginCredentialsValidator
import com.kira.kmpbase.core.ui.generated.resources.Res
import com.kira.kmpbase.core.ui.generated.resources.action_retry
import com.kira.kmpbase.core.ui.generated.resources.error_database
import com.kira.kmpbase.core.ui.generated.resources.error_email_required
import com.kira.kmpbase.core.ui.generated.resources.error_invalid_email
import com.kira.kmpbase.core.ui.generated.resources.error_network_generic
import com.kira.kmpbase.core.ui.generated.resources.error_network_unreachable
import com.kira.kmpbase.core.ui.generated.resources.error_password_min_length
import com.kira.kmpbase.core.ui.generated.resources.error_unknown
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppError.toLocalizedMessage(): String {
    return when (this) {
        is AppError.Validation -> code.toLocalizedMessage()
        is AppError.Network -> toLocalizedNetworkMessage()
        is AppError.Database -> message.ifBlank { stringResource(Res.string.error_database) }
        is AppError.Unknown -> message.ifBlank { stringResource(Res.string.error_unknown) }
    }
}

@Composable
fun ValidationErrorCode.toLocalizedMessage(): String {
    return when (this) {
        ValidationErrorCode.EMAIL_REQUIRED -> stringResource(Res.string.error_email_required)
        ValidationErrorCode.INVALID_EMAIL -> stringResource(Res.string.error_invalid_email)
        ValidationErrorCode.PASSWORD_TOO_SHORT -> stringResource(
            Res.string.error_password_min_length,
            LoginCredentialsValidator.MIN_PASSWORD_LENGTH,
        )
    }
}

@Composable
private fun AppError.Network.toLocalizedNetworkMessage(): String {
    val raw = message
    return when {
        raw.contains("hostname could not be found", ignoreCase = true) ||
            raw.contains("Unable to resolve host", ignoreCase = true) ->
            stringResource(Res.string.error_network_unreachable)
        raw.isBlank() -> stringResource(Res.string.error_network_generic)
        else -> raw
    }
}
