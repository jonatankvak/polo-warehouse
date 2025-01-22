package com.polo.core_ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.polo.core.R.drawable

const val EXPAND_ANIMATION_DURATION = 450

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableCard(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExtended: (isExtended: Boolean) -> Unit,
    titleContent: @Composable () -> Unit,
    bodyContent: @Composable () -> Unit = {},
    controlContent: @Composable () -> Unit = {}
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }

    val transition = updateTransition(transitionState, label = "transition")

    val cardRoundedCorners by transition.animateDp({
        tween(
            durationMillis = EXPAND_ANIMATION_DURATION,
            easing = FastOutSlowInEasing
        )
    }, label = "cornersTransition") {
        if (expanded) 6.dp else 8.dp
    }

    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "arrowRotationDegree")
    {
        if (expanded) 180f else 0f
    }

    Card(
        shape = RoundedCornerShape(cardRoundedCorners),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {

            titleContent()
            ExpandableContent(visible = expanded) {
                Column {
                    bodyContent()
                    controlContent()
                }
            }
        }

        Box(
            modifier = Modifier
                .clickable { onExtended.invoke(expanded) }
                .fillMaxWidth()
                .height(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.rotate(arrowRotationDegree),
                painter = painterResource(
                    id = drawable.ic_expand_more
                ),
                contentDescription = null
            )
        }
    }
}
