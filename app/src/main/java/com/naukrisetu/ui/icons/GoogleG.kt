package com.naukrisetu.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/** Google "G" logo — 4 color paths traced from the Kombai M02 design. */
object GoogleG {
    val Logo: ImageVector =
        ImageVector.Builder(name = "google-g", defaultWidth = 20.dp, defaultHeight = 20.dp, viewportWidth = 18f, viewportHeight = 18f)
            .apply {
                path(fill = SolidColor(Color(0xFF4285F4))) {
                    moveTo(17.64f, 9.2f)
                    curveTo(17.64f, 8.563f, 17.583f, 7.949f, 17.476f, 7.36f)
                    horizontalLineTo(9f)
                    verticalLineTo(10.841f)
                    horizontalLineTo(13.844f)
                    curveTo(13.635f, 11.895f, 12.048f, 13.557f, 9f, 13.557f)
                    curveTo(6.344f, 13.557f, 4.304f, 11.456f, 4.304f, 9f)
                    curveTo(4.304f, 6.544f, 6.344f, 4.443f, 9f, 4.443f)
                    curveTo(10.321f, 4.443f, 11.508f, 4.897f, 12.44f, 5.788f)
                    lineTo(15.022f, 3.208f)
                    curveTo(13.463f, 1.754f, 11.426f, 0.863f, 9f, 0.863f)
                    curveTo(4.486f, 0.863f, 0.787f, 4.562f, 0.787f, 9f)
                    curveTo(0.787f, 13.438f, 4.486f, 17.137f, 9f, 17.137f)
                    curveTo(11.43f, 17.137f, 13.467f, 16.331f, 14.956f, 14.957f)
                    lineTo(12.048f, 12.698f)
                    curveTo(11.242f, 13.238f, 10.211f, 13.558f, 9f, 13.558f)
                    close()
                }
                path(fill = SolidColor(Color(0xFF34A853))) {
                    moveTo(9f, 18f)
                    curveTo(11.43f, 18f, 13.467f, 17.194f, 14.956f, 15.82f)
                    lineTo(12.048f, 13.561f)
                    curveTo(11.242f, 14.101f, 10.163f, 14.421f, 9f, 14.421f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFFBBC05))) {
                    moveTo(3.964f, 10.71f)
                    curveTo(3.784f, 10.117f, 3.682f, 9.407f, 3.682f, 9f)
                    curveTo(3.784f, 8.407f, 3.784f, 7.83f, 3.964f, 7.29f)
                    verticalLineTo(4.958f)
                    horizontalLineTo(0.957f)
                    curveTo(0.348f, 6.41f, 0f, 7.652f, 0f, 9f)
                    curveTo(0.348f, 11.827f, 0.957f, 13.042f, 3.964f, 10.71f)
                    close()
                }
                path(fill = SolidColor(Color(0xFFEA4335))) {
                    moveTo(9f, 3.58f)
                    curveTo(10.321f, 3.58f, 11.508f, 4.034f, 12.44f, 4.925f)
                    lineTo(15.022f, 2.345f)
                    curveTo(13.463f, 0.891f, 11.426f, 0f, 9f, 0f)
                    curveTo(4.486f, 0f, 0.957f, 3.699f, 0.957f, 4.958f)
                    lineTo(3.964f, 7.29f)
                    curveTo(4.672f, 5.163f, 6.656f, 3.58f, 9f, 3.58f)
                    close()
                }
            }.build()
}
