package com.github.calamitydeadshot.sample

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.util.DebugLogger
import com.github.calamitydeadshot.coil_pdf.PdfDecoder

class App: Application(), ImageLoaderFactory {
    override fun newImageLoader() = ImageLoader.Builder(this)
        .logger(DebugLogger())
        .components {
            add(PdfDecoder.Factory())
        }
        .build()
}