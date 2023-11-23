package com.github.calamitydeadshot.coil_pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DecodeResult
import coil.decode.DecodeUtils
import coil.decode.Decoder
import coil.decode.ImageSource
import coil.fetch.SourceResult
import coil.request.Options
import com.github.calamitydeadshot.coil_pdf.pdf.points
import okio.BufferedSource
import kotlin.math.roundToInt

class PdfDecoder constructor(
    private val source: ImageSource,
    private val options: Options
): Decoder {
    private val density = options.context.resources.displayMetrics.density

    override suspend fun decode(): DecodeResult {
        val fileDescriptor = ParcelFileDescriptor.open(
            source.file().toFile(),
            ParcelFileDescriptor.MODE_READ_ONLY
        )
        PdfRenderer(fileDescriptor).use {

            val pageIndex = options.parameters.pdfPage() ?: 0
            require(pageIndex < it.pageCount) { "Page index $pageIndex is out of bounds for ${it.pageCount} pages" }
            val page = it.openPage(0)

            val srcWidth = page.width.points.roundToPx(density)
            val srcHeight = page.height.points.roundToPx(density)

            val dstWidth = options.size.widthPx(options.scale) { srcWidth }
            val dstHeight = options.size.heightPx(options.scale) { srcHeight }
            val scale = DecodeUtils.computeSizeMultiplier(
                srcWidth = srcWidth,
                srcHeight = srcHeight,
                dstWidth = dstWidth,
                dstHeight = dstHeight,
                scale = options.scale
            )
            val width = (scale * srcWidth).roundToInt()
            val height = (scale * srcHeight).roundToInt()

            check(width > 0 && height > 0) {
                "Failed to obtain valid size of a PDF page. Width = $width, height = $height"
            }

            val bitmap = page.renderAndClose(width, height, options)

            return DecodeResult(
                drawable = bitmap.toDrawable(options.context.resources),
                // PDFs can always be re-decoded at a higher resolution
                isSampled = true
            )
        }
    }

    private fun PdfRenderer.Page.renderAndClose(width: Int, height: Int, options: Options) = use {
        val renderMode = options.parameters.pdfRenderMode()?.toRenderMode()
            ?: PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
        val bitmap = options.createBitmap(width, height)
        render(bitmap, null, null, renderMode)
        bitmap
    }


    private fun Options.createBitmap(width: Int, height: Int): Bitmap {
        val backgroundColor = parameters.pdfBackgroundColor() ?: Color.WHITE
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(backgroundColor)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return bitmap
    }

    private fun PdfRenderMode.toRenderMode() = when (this) {
        PdfRenderMode.FOR_DISPLAY -> PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
        PdfRenderMode.FOR_PRINT -> PdfRenderer.Page.RENDER_MODE_FOR_PRINT
    }

    class Factory: Decoder.Factory {
        override fun create(
            result: SourceResult,
            options: Options,
            imageLoader: ImageLoader
        ): Decoder? {
            if (!isApplicable(result.source.source(), result.mimeType)) return null
            return PdfDecoder(result.source, options)
        }

        private fun isApplicable(source: BufferedSource, mimeType: String?): Boolean {
            return (mimeType != null && mimeType == "application/pdf")
                    || DecodeUtils.isPdf(source)
        }
    }

    companion object {
        const val PDF_PAGE_KEY = "coil#pdf_page"
        const val PDF_RENDER_MODE_KEY = "coil#pdf_render_mode"
        const val PDF_BACKGROUND_COLOR = "coil#pdf_background_color"
    }
}