package com.loancaculator.core

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.loancaculator.R
import java.io.File

object PdfShare {
    fun shareResult(context: Context, title: String, summary: String) {
        val document = PdfDocument()
        val page = document.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val paint = Paint().apply { textSize = 18f; isAntiAlias = true }
        page.canvas.drawText(context.getString(R.string.app_name), 48f, 70f, paint)
        paint.textSize = 24f
        page.canvas.drawText(title, 48f, 112f, paint)
        paint.textSize = 15f
        summary.split("|").forEachIndexed { index, entry ->
            val parts = entry.split("=", limit = 2)
            val line = if (parts.size == 2) {
                "${localizedLabel(context, parts[0])}: ${parts[1]}"
            } else {
                entry
            }
            page.canvas.drawText(line, 48f, 160f + index * 32f, paint)
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
        }, context.getString(R.string.share_pdf)))
    }

    private fun localizedLabel(context: Context, label: String): String = when (label.lowercase()) {
        "home price" -> context.getString(R.string.home_price)
        "down payment" -> context.getString(R.string.down_payment)
        "interest rate" -> context.getString(R.string.interest_rate)
        "loan term" -> context.getString(R.string.loan_term)
        "property tax" -> context.getString(R.string.property_tax)
        "pmi" -> context.getString(R.string.pmi)
        "hoa fees" -> context.getString(R.string.hoa_fees)
        "home insurance" -> context.getString(R.string.home_insurance)
        "principal & interest" -> context.getString(R.string.principal_interest)
        "monthly payment" -> context.getString(R.string.monthly_payment)
        "total payment" -> context.getString(R.string.total_payment)
        "total interest" -> context.getString(R.string.total_interest_paid)
        "maturity value" -> context.getString(R.string.maturity_value)
        "total deposited" -> context.getString(R.string.total_deposited)
        "total invested" -> context.getString(R.string.total_invested)
        "interest earned" -> context.getString(R.string.interest_earned)
        else -> label
    }
}
