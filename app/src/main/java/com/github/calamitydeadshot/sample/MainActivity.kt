@file:OptIn(ExperimentalFoundationApi::class)

package com.github.calamitydeadshot.sample

import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.github.calamitydeadshot.coil_pdf.pdfBackground
import com.github.calamitydeadshot.coil_pdf.pdfPage
import com.github.calamitydeadshot.sample.ui.theme.CoilpdfTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val urls = listOf(
            "https://www.orimi.com/pdf-test.pdf",
            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf",
            "https://www.africau.edu/images/default/sample.pdf"
        )

        lifecycleScope.launch {
            saveFiles(urls)
        }

        setContent {
            CoilpdfTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(urls) { url ->
                            var pageCount by remember {
                                mutableStateOf(0)
                            }
                            DisposableEffect(Unit) {
                                val file = File(cacheDir, url.substringAfterLast('/'))
                                if (!file.exists()) return@DisposableEffect onDispose {}
                                val renderer = try {
                                    PdfRenderer(
                                        ParcelFileDescriptor.open(
                                            file,
                                            ParcelFileDescriptor.MODE_READ_ONLY
                                        )
                                    )
                                } catch (e: Exception) {
                                    return@DisposableEffect onDispose {}
                                }
                                pageCount = renderer.pageCount
                                onDispose {
                                    renderer.close()
                                }
                            }
                            val state = rememberPagerState(
                                initialPage = 0,
                                pageCount = { pageCount }
                            )
                            Box {
                                HorizontalPager(
                                    state = state
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.fillMaxWidth(),
                                        model = ImageRequest.Builder(this@MainActivity)
                                            .data(url)
                                            .pdfBackground(Color.White.toArgb())
                                            .pdfPage(it)
                                            .build(),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth
                                    )
                                }

                                Text(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .background(
                                            color = Color.Black.copy(.4f),
                                            shape = CircleShape
                                        )
                                        .padding(8.dp),
                                    text = "Page ${state.currentPage + 1} of $pageCount",
                                    color = Color.White
                                )

                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun saveFiles(urls: List<String>) = withContext(Dispatchers.IO) {
        urls.forEach { url ->
            val file = File(cacheDir, url.substringAfterLast('/'))
            if (!file.exists()) {
                URL(url).openStream().use { input ->
                    File(cacheDir, url.substringAfterLast('/')).apply {
                        createNewFile()
                        val outputStream = outputStream()
                        outputStream.use { output ->
                            val buffer = ByteArray(4 * 1024)
                            while (true) {
                                val byteCount = input.read(buffer)
                                if (byteCount < 0) break
                                output.write(buffer, 0, byteCount)
                            }
                            output.flush()
                        }
                    }
                }
            }
        }
    }
}