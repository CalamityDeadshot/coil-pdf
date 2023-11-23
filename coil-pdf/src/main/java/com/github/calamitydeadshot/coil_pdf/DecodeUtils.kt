package com.github.calamitydeadshot.coil_pdf

import coil.decode.DecodeUtils
import okio.BufferedSource
import okio.ByteString.Companion.encodeUtf8

private val PDF_HEADER = "%PDF".encodeUtf8()

fun DecodeUtils.isPdf(source: BufferedSource): Boolean {
    return source.rangeEquals(0, PDF_HEADER)
}