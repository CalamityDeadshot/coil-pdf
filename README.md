# PDFs
[![](https://jitpack.io/v/CalamityDeadshot/coil-pdf.svg)](https://jitpack.io/#CalamityDeadshot/coil-pdf)

<img src="https://user-images.githubusercontent.com/44675043/285235551-a258e7fa-2e88-4d2a-9cce-9eb3c81c1139.png" alt="image" width="200">

To add PDF support, import the extension library:
```kotlin
implementation("com.github.CalamityDeadshot:coil-pdf:v0.0.5")
```
`settings.gradle.kts`:
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://jitpack.io") }
    }
}
```

And add the decoder to your component registry when constructing your `ImageLoader`:
```kotlin
ImageLoader.Builder(context)
  .components {
      add(PdfDecoder.Factory())
  }
  .build()
```
The ImageLoader will automatically detect and decode any PDFs. This library detects PDFs by looking for the `%PDF` 
magic bytes of the file or `application/pdf` content type. This may not be enough, see [Limitations](#Limitations).

This library uses `PdfRenderer` under the hood.

## Customizations
### Document page
PDFs generally consist of several pages, and you can specify which page to decode by its index:
```kotlin
ImageRequest.Builder(this@MainActivity)
  .data(<your data>)
  .pdfPage(1)
  .build()
```
If page index is out of bounds, `PdfDecoder` throws an `IllegalArgumentException`. Default is 0.

### Document background color
You can specify a background color for a page like this:
```kotlin
ImageRequest.Builder(this@MainActivity)
  .data(<your data>)
  .pdfBackground(Color.White.toArgb())
  .build()
```
Default is White (`0xFFFFFFFF`)

### Render mode
`PdfRenderer` allows for two rendering modes: for display and for printing. You can specify which mode to use:
```kotlin
ImageRequest.Builder(this@MainActivity)
  .data(<your data>)
  .pdfRenderMode(PdfRenderMode.FOR_PRINT)
  .build()
```
Default is `PdfRenderMode.FOR_DISPLAY`

## Limitations
- In the real world a lot of PDF processors used to produce PDF files that break the Adobe PDF specification by
not including `%PDF` bytes in the beginning of the file. This library does not perform error correction of any kind, so
if you are loading a PDF from network, and `application/pdf` content type is not specified, and it is malformed in 
previously descibed way, `PdfDecoder` will decide that it is not applicable for this `Source`.

- **Decoded PDF pages are not interactive**. This library is intended to be used for simple preview use-cases.

## License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
