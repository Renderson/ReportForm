package com.rendersoncs.report.infrastructure.pdf

import android.content.Context
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Environment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import com.rendersoncs.report.R
import com.rendersoncs.report.infrastructure.constants.ReportConstants
import com.rendersoncs.report.model.Report
import com.rendersoncs.report.model.ReportResumeItems
import com.rendersoncs.report.view.MyApplication
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import org.json.JSONException
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PDFGenerator {

    suspend fun generatePDF(report: Report, checkList: ArrayList<ReportResumeItems>): Boolean = suspendCoroutine { continuation ->

        val baseFont = FontFactory.getFont("Helvetica", 12.0f)
        val baseFontBold = FontFactory.getFont("Helvetica-Bold", 12.0f)
        val baseFontBoldList = FontFactory.getFont("Helvetica-Bold", 18.0f)
        val baseFontBoldResult = FontFactory.getFont("Helvetica-Bold", 14.0f)

        val underLine = LineSeparator(
            1.0F,
            100.0F,
            null,
            1,
            -5.0F)

        val lineSpace = 20.0F
        val lineSpaceSmall = 5.0F
        val context = MyApplication.appContext

        try {
            val str =
                FilenameUtils.normalize(String.format(
                    context.resources
                        .getString(R.string.label_name_archive,
                        report.companyFormatter(),
                        report.dateFormatter())))

            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Report"
            )
            if (!filePath.exists()) filePath.mkdirs()
            val file = File(filePath, str)
            if (file.exists()) file.delete()
            file.createNewFile()

            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            val localBitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.logo_bg_white)
            val localByteArrayOutputStream = ByteArrayOutputStream()
            localBitmap.compress(CompressFormat.JPEG, 100, localByteArrayOutputStream)
            val image = Image.getInstance(localByteArrayOutputStream.toByteArray())

            val table = PdfPTable(2)
            table.widthPercentage = 100.0f //Height and width
            table.setWidths(intArrayOf(1, 2)) //setting the height and width
            table.addCell(createImageCell(image))

            var paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_audit),
                    baseFont
                )
            )
            paragraph.spacingAfter = lineSpace
            paragraph.spacingBefore = lineSpace
            paragraph.alignment = 2

            val localPdfPCell1 = PdfPCell()
            localPdfPCell1.addElement(paragraph)
            localPdfPCell1.verticalAlignment = 4
            localPdfPCell1.border = 0
            table.addCell(localPdfPCell1)
            document.add(table)
            document.add(underLine)

            // New paragraph Company
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_company) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.company, baseFont))
            paragraph.alignment = 0
            document.add(paragraph)

            // New paragraph E-mail
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_mail_company) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.email, baseFont))
            paragraph.alignment = 0
            document.add(paragraph)

            // New paragraph Controller
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_audit_officer) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.controller, baseFont))
            paragraph.alignment = 0
            document.add(paragraph)

            // New paragraph Date
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_date) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.date, baseFont))
            paragraph.alignment = 0
            document.add(paragraph)

            // New paragraph Score
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_score) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.score, baseFont))
            paragraph.alignment = 0
            document.add(paragraph)

            // New paragraph Score
            paragraph = Paragraph(
                Chunk(
                    context.resources.getString(R.string.label_paragraph_pdf_result) + " ",
                    baseFontBold
                )
            )
            paragraph.spacingAfter = lineSpaceSmall
            paragraph.spacingBefore = lineSpaceSmall
            paragraph.add(Chunk(report.result, baseFontBoldResult))
            paragraph.alignment = 0
            document.add(paragraph)

            // Create Table Check-List
            paragraph = Paragraph(
                context.resources.getString(R.string.label_paragraph_pdf_report),
                baseFontBoldList
            )
            paragraph.alignment = 1
            document.add(paragraph)
            paragraph = Paragraph("   ")
            document.add(paragraph)

            val pdfTable = PdfPTable(5)
            pdfTable.widthPercentage = 100.0f //Height and width

            val cel1 = PdfPCell(
                Paragraph(
                    context.resources.getString(R.string.label_paragraph_pdf_installations),
                    baseFontBold
                )
            )
            val cel2 = PdfPCell(
                Paragraph(
                    context.resources.getString(R.string.label_paragraph_pdf_description),
                    baseFontBold
                )
            )
            val cel3 = PdfPCell(
                Paragraph(
                    context.resources.getString(R.string.label_paragraph_pdf_conformity),
                    baseFontBold
                )
            )
            val cel4 = PdfPCell(
                Paragraph(
                    context.resources.getString(R.string.label_paragraph_pdf_note),
                    baseFontBold
                )
            )
            val cel5 = PdfPCell(
                Paragraph(
                    context.resources.getString(R.string.label_paragraph_pdf_photo),
                    baseFontBold
                )
            )

            pdfTable.addCell(cel1)
            pdfTable.addCell(cel2)
            pdfTable.addCell(cel3)
            pdfTable.addCell(cel4)
            pdfTable.addCell(cel5)

            addListItems(context, checkList, pdfTable)
            document.add(pdfTable)

            /*Bitmap signatureImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.signature);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            signatureImage.compress(CompressFormat.JPEG, 100, outputStream);
            Image signature = Image.getInstance(outputStream.toByteArray());
            signature.scaleAbsolute(75f, 75f);
            document.add(signature);
            document.add(new Paragraph(context.getResources().getString(R.string.sanitary_signature)));*/
            // Implementation Signature automatic
            /*Image signature = Image.getInstance(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Report-Images/Signature.jpg");
            signature.scaleAbsolute(75f, 75f);
            document.add(signature);
            document.add(new Paragraph(context.getResources().getString(R.string.sanitary_signature)));*/

            document.close()
            continuation.resume(true)
        } catch (e: Exception) {
            continuation.resume(false)
        }
    }

    private fun createImageCell(paramImage: Image): PdfPCell {
        val localPdfPCell = PdfPCell(paramImage, true)
        localPdfPCell.border = 0
        return localPdfPCell
    }

    private fun addListItems(
        context: Context,
        data: ArrayList<ReportResumeItems>,
        pTable: PdfPTable
    ) {
        var celTitle: PdfPCell?
        var celDescription: PdfPCell?
        var celRadio: PdfPCell?
        var celNote: PdfPCell?
        var celImage: PdfPCell
        try {
            var title: String
            var description: String
            var radio: String
            var notes: String
            var photo: String

            data.forEach { report ->
                title = report.title
                description = report.description
                notes = report.note
                radio = report.conformity
                photo = report.photo

                val image: Image = setImagePDF(context, photo)
                celTitle = PdfPCell(Paragraph(title))
                celDescription = PdfPCell(Paragraph(description))
                celRadio = PdfPCell(Paragraph(radio))
                celNote = PdfPCell(Paragraph(notes))
                celImage = PdfPCell(Paragraph(photo))
                celImage.image = image
                celImage.setPadding(5f)
                pTable.addCell(celTitle)
                pTable.addCell(celDescription)
                pTable.addCell(celRadio)
                pTable.addCell(celNote)
                pTable.addCell(celImage)
            }

        } catch (e: JSONException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
    }

    private fun setImagePDF(context: Context, image: String): Image {
        val imageBitmap: Image
        val file = File(image)
        imageBitmap = if (image == ReportConstants.PHOTO.NOT_PHOTO) {
            createBitmap(context)
        } else if (file.exists()) {
            Image.getInstance(image)
        } else {
            createBitmap(context)
        }
        return imageBitmap
    }

    private fun createBitmap(context: Context): Image {
        val image: Image
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.walpaper_not_photo)
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, 100, outputStream)
        val stream = Image.getInstance(outputStream.toByteArray())
        stream.scaleAbsolute(75f, 75f)
        image = Image.getInstance(stream)
        return image
    }
}