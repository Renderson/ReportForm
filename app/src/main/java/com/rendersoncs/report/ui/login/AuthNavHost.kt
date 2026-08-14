package com.rendersoncs.report.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthNavHost(
    onAuthenticated: (String) -> Unit,
    onContactSupport: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.restoreSessionIfLogged()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.restoreSessionIfLogged()
        viewModel.events.collectLatest { event ->
            when (event) {
                is AuthEvent.LoggedIn -> onAuthenticated(event.uid)
                AuthEvent.RecoveryEmailSent -> {
                    if (navController.currentDestination?.route != AuthRoute.RECOVERY_SENT) {
                        navController.navigate(AuthRoute.RECOVERY_SENT) {
                            popUpTo(AuthRoute.FORGOT_PASSWORD) { inclusive = true }
                        }
                    }
                }
                is AuthEvent.Error -> Unit
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.LOGIN,
        modifier = modifier
    ) {
        composable(AuthRoute.LOGIN) {
            LoginScreen(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onPasswordChange = viewModel::onPasswordChange,
                onTogglePassword = viewModel::togglePasswordVisibility,
                onSignIn = viewModel::signIn,
                onForgotPassword = { navController.navigate(AuthRoute.FORGOT_PASSWORD) },
                onSignUp = { navController.navigate(AuthRoute.SIGN_UP) }
            )
        }
        composable(AuthRoute.SIGN_UP) {
            SignUpScreen(
                state = state,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onCargoChange = viewModel::onCargoChange,
                onPasswordChange = viewModel::onPasswordChange,
                onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
                onTogglePassword = viewModel::togglePasswordVisibility,
                onToggleConfirmPassword = viewModel::toggleConfirmPasswordVisibility,
                onSignUp = viewModel::signUp,
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(AuthRoute.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                state = state,
                onEmailChange = viewModel::onEmailChange,
                onSubmit = viewModel::sendRecoveryEmail,
                onBackToLogin = { navController.popBackStack() },
                onContactSupport = onContactSupport
            )
        }
        composable(AuthRoute.RECOVERY_SENT) {
            RecoverySentScreen(
                email = state.recoveryEmail.ifBlank { state.email },
                isLoading = state.isLoading,
                onBackToLogin = {
                    navController.navigate(AuthRoute.LOGIN) {
                        popUpTo(AuthRoute.LOGIN) { inclusive = true }
                    }
                },
                onResend = viewModel::sendRecoveryEmail,
                onContactSupport = onContactSupport
            )
        }
    }
}
