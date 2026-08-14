package com.rendersoncs.report.ui.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.login.components.AuthFeatureIcon
import com.rendersoncs.report.ui.theme.ReportShapes
import com.rendersoncs.report.ui.theme.ReportTheme
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onAbout: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                ProfileEvent.LoggedOut -> onLoggedOut()
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(R.string.label_menu_logout)) },
            text = { Text(stringResource(R.string.profile_logout_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    ProfileContent(
        state = state,
        onChangePassword = onChangePassword,
        onDeleteAccount = onDeleteAccount,
        onDarkThemeChange = viewModel::onDarkThemeChange,
        onAbout = onAbout,
        onLogout = { showLogoutDialog = true },
        modifier = modifier
    )
}

@Composable
private fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileUiState,
    onChangePassword: () -> Unit,
    onDeleteAccount: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onAbout: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        ProfileHeader(state = state)
        Spacer(modifier = Modifier.height(24.dp))
        SectionTitle(stringResource(R.string.label_menu_config))
        Spacer(modifier = Modifier.height(8.dp))
        ProfileMenuCard {
            ProfileMenuItem(
                icon = Icons.Outlined.LockReset,
                label = stringResource(R.string.label_menu_update_password),
                onClick = onChangePassword
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileMenuItem(
                icon = Icons.Outlined.Delete,
                label = stringResource(R.string.label_menu_delete_account),
                onClick = onDeleteAccount,
                destructive = true
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(stringResource(R.string.profile_preferences))
        Spacer(modifier = Modifier.height(8.dp))
        ProfileMenuCard {
            ProfileMenuItem(
                icon = Icons.Outlined.DarkMode,
                label = stringResource(R.string.label_theme),
                onClick = { onDarkThemeChange(!state.darkTheme) },
                leadingIcon = { AnimatedThemeIcon(darkTheme = state.darkTheme) },
                trailing = {
                    Switch(
                        checked = state.darkTheme,
                        onCheckedChange = onDarkThemeChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        SectionTitle(stringResource(R.string.label_menu_others))
        Spacer(modifier = Modifier.height(8.dp))
        ProfileMenuCard {
            ProfileMenuItem(
                icon = Icons.Outlined.Info,
                label = stringResource(R.string.label_menu_about),
                onClick = onAbout
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.Logout,
                label = stringResource(R.string.label_menu_logout),
                onClick = onLogout
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ReportShapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.name.ifBlank { stringResource(R.string.dashboard_tab_profile) },
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = state.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileAvatar() {
    AuthFeatureIcon(
        icon = Icons.Outlined.Person,
        contentDescription = stringResource(R.string.dashboard_tab_profile)
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun ProfileMenuCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = ReportShapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    destructive: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val contentColor = if (destructive) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            leadingIcon()
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
}

@Composable
private fun AnimatedThemeIcon(darkTheme: Boolean) {
    AnimatedContent(
        targetState = darkTheme,
        transitionSpec = {
            (fadeIn(animationSpec = tween(280)) + scaleIn(
                initialScale = 0.5f,
                animationSpec = tween(280)
            )).togetherWith(
                fadeOut(animationSpec = tween(180)) + scaleOut(
                    targetScale = 0.5f,
                    animationSpec = tween(180)
                )
            )
        },
        label = "themeIcon"
    ) { isDark ->
        Icon(
            imageVector = if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ProfileContentPreview() {
    ReportTheme {
        ProfileContent(
            state = ProfileUiState(
                name = "Renderson Cerqueira",
                email = "renderson.silva@gmail.com"
            ),
            onChangePassword = {},
            onDeleteAccount = {},
            onDarkThemeChange = {},
            onAbout = {},
            onLogout = {}
        )
    }
}
