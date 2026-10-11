package com.naukrisetu.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Lucide-style stroke icons (24×24, stroke 2, round caps) drawn as vectors — offline-safe. */
object Lucide {

    private fun icon(paths: PathBuilder.() -> Unit): ImageVector =
        ImageVector.Builder(name = "lucide", defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
            .apply {
                path(
                    fill = SolidColor(Color.Transparent),
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                    pathFillType = PathFillType.NonZero,
                    pathBuilder = paths,
                )
            }.build()


    val Home: ImageVector = icon({
        moveTo(3f, 9.5f); lineTo(12f, 3f); lineTo(21f, 9.5f)
        moveTo(5f, 8.5f); verticalLineTo(20f); horizontalLineTo(19f); verticalLineTo(8.5f)
        moveTo(9f, 20f); verticalLineTo(14f); horizontalLineTo(15f); verticalLineTo(20f)
    })

    /** lucide briefcase-business: body + handle + middle line + clasp. */
    val BriefcaseBusiness: ImageVector = icon({
        moveTo(22f, 17f); arcTo(2f, 2f, 0f, false, true, 20f, 19f)
        horizontalLineTo(4f); arcTo(2f, 2f, 0f, false, true, 2f, 17f)
        verticalLineTo(9f); arcTo(2f, 2f, 0f, false, true, 4f, 7f)
        horizontalLineTo(8f)
        moveTo(6f, 12f); verticalLineTo(7f)
        arcTo(2f, 2f, 0f, false, true, 8f, 5f); horizontalLineTo(16f)
        arcTo(2f, 2f, 0f, false, true, 18f, 7f); verticalLineTo(12f)
        moveTo(22f, 12f); verticalLineTo(17f); arcTo(2f, 2f, 0f, false, true, 20f, 19f)
        moveTo(2f, 12f); horizontalLineTo(20f)
        moveTo(12f, 12f); horizontalLineTo(12.01f)
    })

    val BookOpen: ImageVector = icon({
        moveTo(2f, 4f); curveTo(5f, 3f, 8f, 3f, 12f, 5f); curveTo(16f, 3f, 19f, 3f, 22f, 4f)
        verticalLineTo(19f); curveTo(19f, 19f, 16f, 18f, 12f, 20f); curveTo(8f, 18f, 5f, 19f, 2f, 19f); close()
        moveTo(12f, 5f); verticalLineTo(20f)
    })

    /** lucide user-round: circle head + round shoulders. */
    val UserRound: ImageVector = icon({
        arcTo(5f, 5f, 0f, true, true, 17f, 8f); arcTo(5f, 5f, 0f, true, true, 7f, 8f)
        moveTo(20f, 21f); arcTo(8f, 8f, 0f, false, true, 4f, 21f)
    })

    val Search: ImageVector = icon({
        arcTo(8f, 8f, 0f, true, true, 11f, 19f); arcTo(8f, 8f, 0f, true, true, 11f, 3f)
        moveTo(21f, 21f); lineTo(16.5f, 16.5f)
    })

    val Heart: ImageVector = icon({
        moveTo(19f, 14f); curveTo(17f, 16f, 12f, 21f, 12f, 21f); curveTo(12f, 21f, 7f, 16f, 5f, 14f)
        curveTo(3f, 12f, 3f, 8.5f, 5f, 6.5f); curveTo(7f, 4.5f, 10.5f, 4.5f, 12f, 6.5f); curveTo(13.5f, 4.5f, 17f, 4.5f, 19f, 6.5f)
        curveTo(21f, 8.5f, 21f, 12f, 19f, 14f); close()
    })

    val ChevronRight: ImageVector = icon({
        moveTo(9f, 18f); lineTo(15f, 12f); lineTo(9f, 6f)
    })

    val ChevronDown: ImageVector = icon({
        moveTo(6f, 9f); lineTo(12f, 15f); lineTo(18f, 9f)
    })

    val MapPin: ImageVector = icon({
        moveTo(20f, 10f); curveTo(20f, 16f, 12f, 22f, 12f, 22f); curveTo(12f, 22f, 4f, 16f, 4f, 10f)
        arcTo(8f, 8f, 0f, false, true, 12f, 2f); arcTo(8f, 8f, 0f, false, true, 20f, 10f); close()
        arcTo(3f, 3f, 0f, true, true, 12f, 13f); arcTo(3f, 3f, 0f, true, true, 12f, 7f)
    })

    val Clock: ImageVector = icon({
        arcTo(10f, 10f, 0f, true, true, 12f, 22f); arcTo(10f, 10f, 0f, true, true, 12f, 2f)
        moveTo(12f, 6f); verticalLineTo(12f); lineTo(16f, 14f)
    })

    val Wallet: ImageVector = icon({
        moveTo(21f, 12f); verticalLineTo(7f); horizontalLineTo(5f); arcTo(2f, 2f, 0f, false, false, 3f, 7f); verticalLineTo(17f)
        arcTo(2f, 2f, 0f, false, false, 5f, 19f); horizontalLineTo(21f); verticalLineTo(12f); close()
        moveTo(21f, 12f); horizontalLineTo(16f); arcTo(1f, 1f, 0f, false, false, 16f, 14f); horizontalLineTo(21f)
    })

    val Plus: ImageVector = icon({
        moveTo(12f, 5f); verticalLineTo(19f)
        moveTo(5f, 12f); horizontalLineTo(19f)
    })

    /** lucide plus-circle. */
    val PlusCircle: ImageVector = icon({
        arcTo(10f, 10f, 0f, true, true, 22f, 12f); arcTo(10f, 10f, 0f, true, true, 2f, 12f)
        moveTo(8f, 12f); horizontalLineTo(16f)
        moveTo(12f, 8f); verticalLineTo(16f)
    })

    /** lucide list. */
    val List: ImageVector = icon({
        moveTo(3f, 5f); horizontalLineTo(3.01f)
        moveTo(3f, 12f); horizontalLineTo(3.01f)
        moveTo(3f, 19f); horizontalLineTo(3.01f)
        moveTo(8f, 5f); horizontalLineTo(21f)
        moveTo(8f, 12f); horizontalLineTo(21f)
        moveTo(8f, 19f); horizontalLineTo(21f)
    })

    /** lucide file-check-2. */
    val FileCheck2: ImageVector = icon({
        moveTo(4f, 22f); horizontalLineTo(14f); arcTo(2f, 2f, 0f, false, true, 16f, 20f); verticalLineTo(7f)
        lineTo(11f, 2f); horizontalLineTo(6f); arcTo(2f, 2f, 0f, false, true, 4f, 4f); verticalLineTo(18f)
        moveTo(14f, 2f); verticalLineTo(4f); arcTo(2f, 2f, 0f, false, true, 16f, 6f); horizontalLineTo(11f)
        moveTo(3f, 15f); lineTo(5f, 17f); lineTo(9f, 13f)
    })

    val Camera: ImageVector = icon({
        moveTo(23f, 19f); arcTo(2f, 2f, 0f, false, true, 21f, 21f); horizontalLineTo(3f); arcTo(2f, 2f, 0f, false, true, 1f, 19f)
        verticalLineTo(8f); arcTo(2f, 2f, 0f, false, true, 3f, 6f); horizontalLineTo(7f); lineTo(9f, 3f); horizontalLineTo(15f)
        lineTo(17f, 6f); horizontalLineTo(21f); arcTo(2f, 2f, 0f, false, true, 23f, 8f); close()
        arcTo(4f, 4f, 0f, true, true, 12f, 17f); arcTo(4f, 4f, 0f, true, true, 12f, 9f)
    })

    val FileText: ImageVector = icon({
        moveTo(15f, 2f); horizontalLineTo(6f); arcTo(2f, 2f, 0f, false, false, 4f, 4f); verticalLineTo(20f)
        arcTo(2f, 2f, 0f, false, false, 6f, 22f); horizontalLineTo(18f); arcTo(2f, 2f, 0f, false, false, 20f, 20f)
        verticalLineTo(7f); lineTo(15f, 2f); close()
        moveTo(14f, 2f); verticalLineTo(6f); horizontalLineTo(20f)
        moveTo(9f, 13f); horizontalLineTo(15f)
        moveTo(9f, 17f); horizontalLineTo(15f)
    })

    val Info: ImageVector = icon({
        arcTo(10f, 10f, 0f, true, true, 12f, 22f); arcTo(10f, 10f, 0f, true, true, 12f, 2f)
        moveTo(12f, 16f); verticalLineTo(12f)
        moveTo(12f, 8f); horizontalLineTo(12.01f)
    })

    val Check: ImageVector = icon({
        moveTo(20f, 6f); lineTo(9f, 17f); lineTo(4f, 12f)
    })

    val ShieldCheck: ImageVector = icon({
        moveTo(20f, 13f); curveTo(20f, 17.5f, 16.5f, 20.5f, 12f, 22f); curveTo(7.5f, 20.5f, 4f, 17.5f, 4f, 13f)
        verticalLineTo(6f); lineTo(12f, 2f); lineTo(20f, 6f); verticalLineTo(13f); close()
        moveTo(9f, 12f); lineTo(11f, 14f); lineTo(15f, 10f)
    })
}

private typealias PathBuilder = androidx.compose.ui.graphics.vector.PathBuilder
