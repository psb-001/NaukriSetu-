package com.naukrisetu.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.naukrisetu.ui.theme.ThemeColors

/** Pure math for the ring — unit-testable. */
fun sweepFor(score: Int): Float = score.coerceIn(0, 100) / 100f * 360f

/**
 * Kombai score ring: grey track + emerald arc (round caps) + centered score.
 * Matches the M07/M08 "progress ring" designs.
 */
@Composable
fun ScoreRing(score: Int, size: Dp = 52.dp, stroke: Dp = 5.dp, fontSize: Int = 15) {
    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val w = size.toPx()
            val pad = stroke.toPx() / 2 + 1f
            drawArc(
                color = androidx.compose.ui.graphics.Color(0xFFEBEBEB),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(pad, pad),
                size = androidx.compose.ui.geometry.Size(w - pad * 2, w - pad * 2),
                style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
            )
            if (score > 0) {
                drawArc(
                    color = ThemeColors.Primary,
                    startAngle = -90f,
                    sweepAngle = sweepFor(score),
                    useCenter = false,
                    topLeft = Offset(pad, pad),
                    size = androidx.compose.ui.geometry.Size(w - pad * 2, w - pad * 2),
                    style = Stroke(width = stroke.toPx(), cap = StrokeCap.Round),
                )
            }
        }
        Text("$score", fontSize = fontSize.sp, fontWeight = FontWeight.Bold, color = ThemeColors.Ink)
    }
}
