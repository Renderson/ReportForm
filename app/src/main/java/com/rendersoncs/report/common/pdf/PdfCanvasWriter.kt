package com.rendersoncs.report.common.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportResumeItems
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class PdfCanvasWriter(private val context: Context) {

    private val document = PdfDocument()
    private var page: PdfDocument.Page? = null
    private lateinit var canvas: Canvas
    private var pageIndex = 0
    private var cursor = TOP
    private var generatedAt = ""

    fun write(report: Report, items: List<ReportResumeItems>, file: File): Boolean {
        return try {
            generatedAt = context.getString(
                R.string.pdf_generated_at,
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )
            startPage()
            drawHeader(report)
            drawMeta(report)
            drawSectionTitle(context.getString(R.string.pdf_details_title))
            items.forEach { item -> drawCard(item) }
            finishPage()
            file.parentFile?.mkdirs()
            file.outputStream().use { document.writeTo(it) }
            document.close()
            file.exists() && file.length() > 0
        } catch (error: Exception) {
            FirebaseCrashlytics.getInstance().recordException(error)
            runCatching { document.close() }
            runCatching { if (file.exists()) file.delete() }
            false
        }
    }

    private fun drawHeader(report: Report) {
        val result = report.result.orEmpty().ifBlank { context.getString(R.string.according) }
        val isConform = result.equals("CONFORME", ignoreCase = true) ||
            result.equals("ACCORDING", ignoreCase = true)
        val badgeText = context.getString(
            R.string.pdf_result_badge,
            result.uppercase(Locale.getDefault())
        )
        val scoreText = context.getString(R.string.pdf_score_line, formatScore(report.score))
        val badgeFg = if (isConform) CONFORM else NC
        val badgeBg = if (isConform) CONFORM_BG else NC_BG

        val titlePaint = textPaint(22f, true)
        canvas.drawText(context.getString(R.string.title_report), LEFT, cursor + 20f, titlePaint)
        cursor += 28f
        canvas.drawText(
            context.getString(R.string.pdf_subtitle),
            LEFT,
            cursor + 12f,
            textPaint(9f, color = MUTED)
        )
        cursor += 16f
        canvas.drawText(
            context.getString(R.string.pdf_doc_id, documentId(report)),
            LEFT,
            cursor + 12f,
            textPaint(8f, color = MUTED)
        )

        val badgePaint = textPaint(8f, true, badgeFg)
        val badgeWidth = badgePaint.measureText(badgeText) + 24f
        val badgeLeft = RIGHT - badgeWidth
        val badgeRect = RectF(badgeLeft, TOP, RIGHT, TOP + 22f)
        canvas.drawRoundRect(badgeRect, 11f, 11f, fill(badgeBg))
        canvas.drawText(badgeText, badgeLeft + 12f, TOP + 15f, badgePaint)

        val scorePaint = textPaint(12f, true)
        val scoreWidth = scorePaint.measureText(scoreText)
        val scoreX = RIGHT - scoreWidth
        canvas.drawText(scoreText, scoreX, TOP + 44f, scorePaint)
        canvas.drawLine(scoreX, TOP + 48f, RIGHT, TOP + 48f, stroke(INK, 0.8f))

        cursor = TOP + 64f
    }

    private fun drawMeta(report: Report) {
        val fields = listOf(
            context.getString(R.string.pdf_meta_company) to report.company,
            context.getString(R.string.pdf_meta_contact) to report.email,
            context.getString(R.string.pdf_meta_auditor) to report.controller,
            context.getString(R.string.pdf_meta_date) to report.date
        )
        val blockHeight = 28f
        val boxHeight = 16f + fields.size * blockHeight
        ensureSpace(boxHeight + 12f)
        val box = RectF(LEFT, cursor, RIGHT, cursor + boxHeight)
        canvas.drawRoundRect(box, 8f, 8f, fill(BOX))
        var inner = cursor + 14f
        fields.forEach { (label, value) ->
            canvas.drawText(
                label.uppercase(Locale.getDefault()),
                LEFT + 12f,
                inner,
                textPaint(7f, true, MUTED)
            )
            canvas.drawText(
                value.orEmpty().ifBlank { "—" },
                LEFT + 12f,
                inner + 14f,
                textPaint(11f, true)
            )
            inner += blockHeight
        }
        cursor = box.bottom + 16f
    }

    private fun drawSectionTitle(text: String) {
        ensureSpace(28f)
        canvas.drawText(text, LEFT, cursor + 14f, textPaint(13f, true, ACCENT))
        cursor += 24f
    }

    private fun drawCard(item: ReportResumeItems) {
        val style = statusStyle(item.conformity)
        val contentLeft = LEFT + 14f
        val contentWidth = WIDTH - 26f
        val title = item.title.ifBlank { "—" }
        val description = item.description.ifBlank { "—" }
        val note = item.note.ifBlank { context.getString(R.string.label_not_observation) }
        val descLayout = layout(description, textPaint(9f, color = MUTED), contentWidth)
        val noteLayout = layout(note, textPaint(9f, color = style.noteColor), contentWidth - 16f)
        val photo = loadPhoto(item.photo)
        val photoBlock = if (photo != null) 18f + PHOTO_HEIGHT else 0f
        val height = 18f + 22f + descLayout.height + 12f + 18f + noteLayout.height + photoBlock + 20f
        ensureSpace(height)

        val card = RectF(LEFT, cursor, RIGHT, cursor + height)
        val corner = 8f
        val cardPath = Path().apply { addRoundRect(card, corner, corner, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(cardPath)
        canvas.drawRect(card, fill(WHITE))
        canvas.drawRect(LEFT, cursor, LEFT + 6f, cursor + height, fill(style.accent))
        canvas.restore()
        canvas.drawRoundRect(card, corner, corner, stroke(BORDER, 0.8f))

        var inner = cursor + 14f
        val statusPaint = textPaint(7f, true, style.accent)
        val statusWidth = statusPaint.measureText(style.badge) + 16f
        val statusRect = RectF(RIGHT - 12f - statusWidth, inner, RIGHT - 12f, inner + 16f)
        canvas.drawRoundRect(statusRect, 4f, 4f, fill(style.badgeBg))
        canvas.drawText(style.badge, statusRect.left + 8f, inner + 11f, statusPaint)

        val titleWidth = (statusRect.left - 8f - contentLeft).coerceAtLeast(40f)
        val titleLayout = layout(title, textPaint(11f, true), titleWidth)
        drawLayout(titleLayout, contentLeft, inner)
        inner += 22f

        drawLayout(descLayout, contentLeft, inner)
        inner += descLayout.height + 10f

        val noteBox = RectF(contentLeft, inner, RIGHT - 12f, inner + 16f + noteLayout.height)
        canvas.drawRoundRect(noteBox, 6f, 6f, fill(NOTE_BG))
        canvas.drawText(
            style.noteLabel.uppercase(Locale.getDefault()),
            contentLeft + 8f,
            inner + 12f,
            textPaint(7f, true, ACCENT)
        )
        drawLayout(noteLayout, contentLeft + 8f, inner + 16f)
        inner = noteBox.bottom + 8f

        if (photo != null) {
            canvas.drawText(
                context.getString(R.string.pdf_photo_evidence).uppercase(Locale.getDefault()),
                contentLeft,
                inner + 10f,
                textPaint(7f, true, MUTED)
            )
            val dest = RectF(
                contentLeft,
                inner + 16f,
                contentLeft + PHOTO_WIDTH,
                inner + 16f + PHOTO_HEIGHT
            )
            canvas.drawBitmap(photo, null, dest, Paint(Paint.ANTI_ALIAS_FLAG))
            canvas.drawRoundRect(dest, 4f, 4f, stroke(BORDER, 0.5f))
            photo.recycle()
        }
        cursor = card.bottom + 10f
    }

    private fun statusStyle(conformity: Int): ItemStyle {
        return when (conformity) {
            ReportConstants.ITEM.OPT_NUM1 -> ItemStyle(
                accent = CONFORM,
                badgeBg = CONFORM_BG,
                badge = context.getString(R.string.pdf_status_conforme),
                noteLabel = context.getString(R.string.pdf_notes),
                noteColor = INK
            )
            ReportConstants.ITEM.OPT_NUM2 -> ItemStyle(
                accent = NA,
                badgeBg = NA_BG,
                badge = context.getString(R.string.pdf_status_nao_aplicavel),
                noteLabel = context.getString(R.string.pdf_justification),
                noteColor = INK
            )
            else -> ItemStyle(
                accent = NC,
                badgeBg = NC_BG,
                badge = context.getString(R.string.pdf_status_nao_conforme),
                noteLabel = context.getString(R.string.pdf_notes_critical),
                noteColor = NC
            )
        }
    }

    private fun startPage() {
        pageIndex += 1
        val info = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIndex).create()
        page = document.startPage(info)
        canvas = page!!.canvas
        cursor = TOP
        canvas.drawColor(WHITE)
    }

    private fun finishPage() {
        val current = page ?: return
        canvas.drawLine(LEFT, PAGE_HEIGHT - 32f, RIGHT, PAGE_HEIGHT - 32f, stroke(BORDER, 0.6f))
        canvas.drawText(generatedAt, LEFT, PAGE_HEIGHT - 18f, textPaint(8f, color = MUTED))
        val pagePaint = textPaint(8f, color = MUTED)
        val pageLabel = context.getString(R.string.pdf_page_number, pageIndex)
            .replace(Regex("""\s+(de|of)$""", RegexOption.IGNORE_CASE), "")
            .trim()
        canvas.drawText(
            pageLabel,
            RIGHT - pagePaint.measureText(pageLabel),
            PAGE_HEIGHT - 18f,
            pagePaint
        )
        document.finishPage(current)
        page = null
    }

    private fun ensureSpace(needed: Float) {
        if (cursor + needed <= BOTTOM) return
        finishPage()
        startPage()
    }

    private fun drawLayout(layout: StaticLayout, x: Float, y: Float) {
        canvas.save()
        canvas.translate(x, y)
        layout.draw(canvas)
        canvas.restore()
    }

    private fun layout(text: String, paint: TextPaint, width: Float): StaticLayout {
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width.toInt().coerceAtLeast(1))
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setIncludePad(false)
            .build()
    }

    private fun textPaint(
        size: Float,
        bold: Boolean = false,
        color: Int = INK
    ): TextPaint {
        return TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            textSize = size
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.SANS_SERIF
        }
    }

    private fun fill(color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }

    private fun stroke(color: Int, width: Float) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.STROKE
        strokeWidth = width
    }

    private fun loadPhoto(path: String): Bitmap? {
        if (path.isBlank() || path == ReportConstants.PHOTO.NOT_PHOTO) return null
        val file = File(path)
        if (!file.exists()) return null
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(path, bounds)
            var sample = 1
            while (bounds.outWidth / sample > 900 || bounds.outHeight / sample > 900) {
                sample *= 2
            }
            BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
        }.getOrNull()
    }

    private fun documentId(report: Report): String {
        val year = runCatching {
            val parsed = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .parse(report.date.orEmpty())
            SimpleDateFormat("yyyy", Locale.getDefault()).format(parsed!!)
        }.getOrElse {
            SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())
        }
        return "REP-$year-${report.id ?: 0}"
    }

    private fun formatScore(raw: String?): String {
        val value = raw.orEmpty().replace(",", ".").substringBefore("/").trim()
        val number = value.toFloatOrNull() ?: return raw.orEmpty().ifBlank { "—" }
        return if (number % 1f == 0f) {
            number.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", number)
        }
    }

    private data class ItemStyle(
        val accent: Int,
        val badgeBg: Int,
        val badge: String,
        val noteLabel: String,
        val noteColor: Int
    )

    private companion object {
        const val PAGE_WIDTH = 595
        const val PAGE_HEIGHT = 842
        const val LEFT = 36f
        const val RIGHT = 559f
        const val WIDTH = RIGHT - LEFT
        const val TOP = 42f
        const val BOTTOM = 794f
        const val PHOTO_WIDTH = 120f
        const val PHOTO_HEIGHT = 84f
        const val WHITE = 0xFFFFFFFF.toInt()
        const val INK = 0xFF111111.toInt()
        const val MUTED = 0xFF6B7280.toInt()
        const val BORDER = 0xFFE5E7EB.toInt()
        const val BOX = 0xFFF3F4F6.toInt()
        const val NOTE_BG = 0xFFEFF6FF.toInt()
        const val ACCENT = 0xFF1565C0.toInt()
        const val CONFORM = 0xFF2E7D32.toInt()
        const val CONFORM_BG = 0xFFE8F5E9.toInt()
        const val NC = 0xFFC62828.toInt()
        const val NC_BG = 0xFFFFEBEE.toInt()
        const val NA = 0xFF607D8B.toInt()
        const val NA_BG = 0xFFEEEEEE.toInt()
    }
}
