package com.rendersoncs.report.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

class SnackbarFabState(val hostState: SnackbarHostState) {
    var snackbarHeightPx by mutableIntStateOf(0)
}

@Composable
fun rememberSnackbarFabState(
    hostState: SnackbarHostState = remember { SnackbarHostState() }
): SnackbarFabState {
    return remember(hostState) { SnackbarFabState(hostState) }
}

@Composable
private fun ReportSnackbarHost(
    modifier: Modifier = Modifier,
    state: SnackbarFabState
) {
    SnackbarHost(hostState = state.hostState, modifier = modifier) { data ->
        Snackbar(
            snackbarData = data,
            modifier = Modifier.onSizeChanged { state.snackbarHeightPx = it.height }
        )
    }
}

@Composable
fun SnackbarBottomOverlay(
    modifier: Modifier = Modifier,
    state: SnackbarFabState,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        ReportSnackbarHost(
            state = state,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun Modifier.paddingAboveSnackbar(state: SnackbarFabState): Modifier {
    val density = LocalDensity.current
    val lift by animateDpAsState(
        targetValue = if (state.hostState.currentSnackbarData != null) {
            with(density) { state.snackbarHeightPx.toDp() + 8.dp }
        } else {
            0.dp
        },
        label = "fabAboveSnackbar"
    )
    return this.padding(bottom = lift)
}
