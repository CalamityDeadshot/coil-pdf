package com.github.calamitydeadshot.coil_pdf.pdf

import kotlin.math.roundToInt

/**
 * Represents a size in PDF points. One point is equal to 1/72 of an inch
 */
@JvmInline
internal value class PdfSize(val points: Int) {

    fun toPx(density: Float): Float {
        val dp = points * dpToPointCoefficient
        return dp * density
    }

    fun roundToPx(density: Float) = toPx(density).roundToInt()
}

// 1 dp is (1/160)", 1 point is (1/72)", so 1 dp is exactly (72/160 = .45) * points
private const val dpToPointCoefficient = .45f

internal val Int.points: PdfSize
    get() = PdfSize(this)