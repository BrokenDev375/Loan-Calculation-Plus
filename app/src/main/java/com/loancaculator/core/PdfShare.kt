package com.loancaculator.core

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File

object PdfShare {
    fun shareResult(context: Context, title: String, summary: String) {
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val paint = Paint().apply { textSize = 18f; isAntiAlias = true }
        page.canvas.drawText("Loan Calculation Plus", 48f, 70f, paint)
        paint.textSize = 24f
        page.canvas.drawText(title, 48f, 112f, paint)
        paint.textSize = 15f
        summary.split("|").forEachIndexed { index, entry ->
            page.canvas.drawText(entry.replace("=", ": "), 48f, 160f + index * 32f, paint)
        }
        document.finishPage(page)
        val file = File(context.cacheDir, "loan-calculation-result.pdf")
        file.outputStream().use { document.writeTo(it) }
        document.close()
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, "Share result"))
    }
}
