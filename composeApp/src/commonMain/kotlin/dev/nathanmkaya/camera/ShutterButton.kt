@file:Suppress("unused")

package dev.nathanmkaya.camera

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShutterButton(
    modifier: Modifier = Modifier,
    size: Dp = 74.dp,           // outer diameter
    ringWidth: Dp = 6.dp,       // thickness of the outer ring
    enabled: Boolean = true,
    onClick: () -> Unit,        // tap to take photo
    onLongPress: (() -> Unit)? = null // hold to start video (optional)
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val haptics = LocalHapticFeedback.current

    // Subtle press shrink
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.93f else 1f,
        label = "press-scale"
    )

    // Build the clickable/long-press behavior
    val clickMod = Modifier
        .combinedClickable(
            interactionSource = interaction,
            indication = ripple(bounded = false, radius = size / 2),
            enabled = enabled,
            onClick = {
                haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
            onLongClick = onLongPress?.let {
                { haptics.performHapticFeedback(HapticFeedbackType.LongPress); it() }
            }
        )

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "Camera shutter" }
            .then(clickMod) // put interactions early so ripple draws correctly
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .border(width = ringWidth, color = Color.Black, shape = CircleShape)
            .clip(CircleShape)
            .background(if (isPressed && enabled) Color(0xFFEDEDED) else Color.White),
        contentAlignment = Alignment.Center
    ) { /* Empty on purpose – visuals are handled by the modifiers */ }
}