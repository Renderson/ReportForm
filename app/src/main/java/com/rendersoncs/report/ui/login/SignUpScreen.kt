package com.rendersoncs.report.ui.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.login.components.AuthCard
import com.rendersoncs.report.ui.login.components.AuthDropdownField
import com.rendersoncs.report.ui.login.components.AuthFeatureIcon
import com.rendersoncs.report.ui.login.components.AuthTextField
import com.rendersoncs.report.ui.theme.ReportShapes
import com.rendersoncs.report.ui.theme.ReportTheme

@Composable
fun SignUpScreen(
    state: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onCargoChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onSignUp: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val roles = stringArrayResource(R.array.auth_roles).toList()

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthCard {
                AuthFeatureIcon(
                    icon = Icons.Outlined.Shield,
                    contentDescription = stringResource(R.string.auth_sign_up_title),
                    filled = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.auth_sign_up_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.auth_sign_up_subtitle, stringResource(R.string.app_name)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                AuthTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = stringResource(R.string.auth_full_name),
                    placeholder = stringResource(R.string.auth_full_name_placeholder),
                    leadingIcon = Icons.Outlined.Person,
                    isError = state.nameError,
                    errorText = if (state.nameError) {
                        stringResource(R.string.label_sign_insert_name)
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.Words
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.auth_corporate_email),
                    placeholder = stringResource(R.string.auth_corporate_email_placeholder),
                    leadingIcon = Icons.Outlined.Email,
                    isError = state.emailError,
                    errorText = if (state.emailError) {
                        stringResource(R.string.txt_email)
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthDropdownField(
                    value = state.cargo,
                    options = roles,
                    onValueChange = onCargoChange,
                    label = stringResource(R.string.auth_role),
                    placeholder = stringResource(R.string.auth_role_placeholder),
                    leadingIcon = Icons.Outlined.WorkOutline,
                    isError = state.cargoError,
                    errorText = if (state.cargoError) {
                        stringResource(R.string.auth_role_required)
                    } else {
                        null
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.password),
                    placeholder = stringResource(R.string.password),
                    leadingIcon = Icons.Outlined.Lock,
                    isError = state.passwordError,
                    errorText = if (state.passwordError) {
                        stringResource(R.string.auth_password_min_length)
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next,
                    isPassword = true,
                    passwordVisible = state.passwordVisible,
                    onTogglePassword = onTogglePassword
                )
                Spacer(modifier = Modifier.height(12.dp))
                AuthTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = stringResource(R.string.auth_confirm_password),
                    placeholder = stringResource(R.string.auth_confirm_password),
                    leadingIcon = Icons.Outlined.LockReset,
                    isError = state.confirmPasswordError,
                    errorText = if (state.confirmPasswordError) {
                        stringResource(R.string.auth_password_mismatch)
                    } else {
                        null
                    },
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    isPassword = true,
                    passwordVisible = state.confirmPasswordVisible,
                    onTogglePassword = onToggleConfirmPassword,
                    onImeAction = onSignUp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onSignUp,
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = ReportShapes.small,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.auth_sign_up_title),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.auth_already_have_account),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = stringResource(R.string.auth_back_to_login),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SignUpScreenPreview() {
    ReportTheme {
        SignUpScreen(
            state = AuthUiState(),
            onNameChange = {},
            onEmailChange = {},
            onCargoChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePassword = {},
            onToggleConfirmPassword = {},
            onSignUp = {},
            onBackToLogin = {}
        )
    }
}
