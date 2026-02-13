@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.polo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.AutoMirrored.Filled
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.animateTo
import kotlin.math.roundToInt

private const val SnapThreshold = 0.8f

@Composable
fun SlideToUnlock(
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onUnlockRequested: () -> Unit,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val onUnlockRequestedState by rememberUpdatedState(onUnlockRequested)
    val initialAnchors = remember {
        DraggableAnchors {
            Anchor.Start at 0f
            Anchor.End at 0f
        }
    }
    val swipeState = remember {
        AnchoredDraggableState(
            if (isLoading) Anchor.End else Anchor.Start,
            initialAnchors
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = swipeState,
        positionalThreshold = { distance: Float -> distance * SnapThreshold },
        animationSpec = spring()
    )

    LaunchedEffect(isLoading) {
        swipeState.animateTo(if (isLoading) Anchor.End else Anchor.Start)
    }

    var hasTriggered by remember { mutableStateOf(false) }
    LaunchedEffect(swipeState.currentValue, isLoading) {
        when (swipeState.currentValue) {
            Anchor.End -> if (!hasTriggered && !isLoading) {
                hasTriggered = true
                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                onUnlockRequestedState()
            }
            Anchor.Start -> hasTriggered = false
        }
    }

    Track(
        swipeState = swipeState,
        flingBehavior = flingBehavior,
        enabled = !isLoading,
        modifier = modifier,
    ) { swipeFraction ->
        Hint(
            text = text,
            swipeFraction = swipeFraction,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(PaddingValues(horizontal = Thumb.Size + 8.dp)),
        )

        Thumb(
            isLoading = isLoading,
            modifier = Modifier.offset {
                IntOffset(swipeState.requireOffset().roundToInt(), 0)
            },
        )
    }
}

enum class Anchor { Start, End }

@Composable
fun Track(
    swipeState: AnchoredDraggableState<Anchor>,
    flingBehavior: FlingBehavior,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.(Float) -> Unit,
) {
    val density = LocalDensity.current
    var endOfTrackPx by remember { mutableFloatStateOf(0f) }

    val horizontalPadding = 10.dp

    val startOfTrackPx = 0f

    val swipeFraction by remember {
        derivedStateOf {
            if (endOfTrackPx <= 0f || swipeState.offset.isNaN()) {
                0f
            } else {
                (swipeState.requireOffset() / endOfTrackPx).coerceIn(0f, 1f)
            }
        }
    }

    val colorScheme = MaterialTheme.colorScheme
    val backgroundColor by remember(swipeFraction, colorScheme) {
        derivedStateOf {
            calculateTrackColor(
                swipeFraction = swipeFraction,
                startColor = colorScheme.primaryContainer,
                endColor = colorScheme.secondaryContainer,
            )
        }
    }

    Box(
        modifier = modifier
            .onSizeChanged { size ->
                endOfTrackPx = with(density) {
                    size.width - (2 * horizontalPadding + Thumb.Size).toPx()
                }
                if (endOfTrackPx > 0f) {
                    val anchors = DraggableAnchors {
                        Anchor.Start at startOfTrackPx
                        Anchor.End at endOfTrackPx
                    }
                    swipeState.updateAnchors(anchors)
                }
            }
            .height(56.dp)
            .fillMaxWidth()
            .anchoredDraggable(
                enabled = enabled,
                state = swipeState,
                orientation = Orientation.Horizontal,
                flingBehavior = flingBehavior
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(
                PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = 8.dp,
                )
            ),
        content = { content(swipeFraction) },
    )
}

fun calculateTrackColor(
    swipeFraction: Float,
    startColor: Color,
    endColor: Color,
): Color {
    val endOfColorChangeFraction = 0.4f
    val fraction = (swipeFraction / endOfColorChangeFraction).coerceIn(0f..1f)
    return lerp(startColor, endColor, fraction)
}

@Composable
fun Thumb(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(Thumb.Size)
            .background(color = colorScheme.primary, shape = RoundedCornerShape(12.dp))
            .padding(8.dp),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(2.dp),
                color = colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Icon(
                imageVector = Filled.ArrowForward,
                contentDescription = null,
                tint = colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun Hint(
    text: String,
    swipeFraction: Float,
    modifier: Modifier = Modifier,
) {
    val onTrackColor = MaterialTheme.colorScheme.onPrimary
    val hintTextColor by remember(swipeFraction, onTrackColor) {
        derivedStateOf { calculateHintTextColor(swipeFraction, onTrackColor) }
    }

    Text(
        text = text,
        color = hintTextColor,
        style = MaterialTheme.typography.labelLarge,
        modifier = modifier
    )
}

fun calculateHintTextColor(
    swipeFraction: Float,
    startColor: Color,
): Color {
    val endOfFadeFraction = 0.35f
    val fraction = (swipeFraction / endOfFadeFraction).coerceIn(0f..1f)
    return lerp(startColor, startColor.copy(alpha = 0f), fraction)
}


private object Thumb {
    val Size = 48.dp
}

private object Track
