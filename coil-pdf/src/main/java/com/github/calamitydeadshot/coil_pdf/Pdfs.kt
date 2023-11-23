package com.github.calamitydeadshot.coil_pdf

import android.graphics.Color
import coil.request.ImageRequest
import coil.request.Parameters
import com.github.calamitydeadshot.coil_pdf.PdfDecoder.Companion.PDF_BACKGROUND_COLOR
import com.github.calamitydeadshot.coil_pdf.PdfDecoder.Companion.PDF_PAGE_KEY
import com.github.calamitydeadshot.coil_pdf.PdfDecoder.Companion.PDF_RENDER_MODE_KEY

/**
 * Set the page index to extract from a PDF document.
 *
 * Default: 0
 */
fun ImageRequest.Builder.pdfPage(page: Int): ImageRequest.Builder {
    require(page >= 0) { "page in a PDF document cannot be negative (got $page)" }
    return setParameter(PDF_PAGE_KEY, page)
}

/**
 * Specifies PDF render mode.
 */
enum class PdfRenderMode {
    /**
     * Mode to render the content for display on a screen.
     */
    FOR_DISPLAY,
    /**
     * Mode to render the content for printing.
     */
    FOR_PRINT
}

/**
 * Set the render mode with which to render a PDF document page.
 *
 * Default is [PdfRenderMode.FOR_DISPLAY]
 */
fun ImageRequest.Builder.pdfRenderMode(mode: PdfRenderMode) =
    setParameter(PDF_RENDER_MODE_KEY, mode)

/**
 * Set a color to use as a page background.
 *
 * Default is [Color.WHITE]
 */
fun ImageRequest.Builder.pdfBackground(color: Int) =
    setParameter(PDF_BACKGROUND_COLOR, color)

/**
 * Get the page index to extract from a PDF document.
 */
fun Parameters.pdfPage(): Int? = value(PDF_PAGE_KEY)

fun Parameters.pdfRenderMode(): PdfRenderMode? = value(PDF_RENDER_MODE_KEY)

fun Parameters.pdfBackgroundColor(): Int? = value(PDF_BACKGROUND_COLOR)