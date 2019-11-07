package com.rendersoncs.reportform.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.List;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.rendersoncs.reportform.R;
import com.rendersoncs.reportform.constants.ReportConstants;
import com.rendersoncs.reportform.itens.ReportItems;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CreatePDFViewer {

    private static final Font baseFont = FontFactory.getFont("Helvetica", 12.0F);
    private static final Font baseFontBold = FontFactory.getFont("Helvetica-Bold", 12.0F);
    private static final Font baseFontBoldList = FontFactory.getFont("Helvetica-Bold", 18.0F);
    private static final LineSeparator UNDERLINE;
    private static final Float lineSpace;
    private static final Float lineSpaceSmall;


    static {
        lineSpace = 20.0F;
        lineSpaceSmall = 5.0F;
        UNDERLINE = new LineSeparator(1.0F, 100.0F, null, 1, -5.0F);
    }

    public File write(Context context, ReportItems paramRepo) throws Exception {
        List listTitle = new List();
        String date = paramRepo.getDate();

        /*Object[] arrayOfObject = new Object[2];
        arrayOfObject[0] = paramRepo.getCompany().replaceAll(" ", "_");
        arrayOfObject[1] = new SimpleDateFormat("dd-MM-yyyy").format(paramRepo.getDate()).trim();
        Log.i("PDF", "Generate PDF!!!! " + arrayOfObject + " Nome arrayOfObject");
        //arrayOfObject[1] = date;
        String str = FilenameUtils.normalize(String.format("Report-%s-%s.pdf", arrayOfObject));*/

        String str = FilenameUtils.normalize(String.format(context.getResources().getString(R.string.label_name_archive), paramRepo.getCompany(), paramRepo.getDate()));
        Log.i("PDF", "Generate PDF!!!! " + str + " File Name");

        File mFilePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Report");
        if (!mFilePath.exists())
            mFilePath.mkdirs();
        File localFile2 = new File(mFilePath, str);
        if (localFile2.exists())
            localFile2.delete();
        localFile2.createNewFile();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(localFile2));
        document.open();
        document.addAuthor("Render");

        Bitmap localBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.logo_bg_white);
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        localBitmap.compress(CompressFormat.JPEG, 100, localByteArrayOutputStream);
        Image image = Image.getInstance(localByteArrayOutputStream.toByteArray());

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100.0F);//Height and width
        table.setWidths(new int[]{1, 2});//setting the height and width
        table.addCell(createImageCell(image));

        Paragraph paragraph = new Paragraph(new Chunk(context.getResources().getString(R.string.label_paragraph_pdf_audit), baseFont));
        paragraph.setSpacingAfter(lineSpace);
        paragraph.setSpacingBefore(lineSpace);
        paragraph.setAlignment(2);
        PdfPCell localPdfPCell1 = new PdfPCell();
        localPdfPCell1.addElement(paragraph);
        localPdfPCell1.setVerticalAlignment(4);
        localPdfPCell1.setBorder(0);
        table.addCell(localPdfPCell1);
        document.add(table);
        document.add(UNDERLINE);

        //New paragraph Company
        paragraph = new Paragraph(new Chunk(context.getResources().getString(R.string.label_paragraph_pdf_company), baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getCompany(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        //New paragraph E-mail
        paragraph = new Paragraph(new Chunk(context.getResources().getString(R.string.label_paragraph_pdf_mail_company), baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getEmail(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        //New paragraph Date
        paragraph = new Paragraph(new Chunk(context.getResources().getString(R.string.label_paragraph_pdf_date), baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getDate(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        // Create Table Check-List
        paragraph = new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_report), baseFontBoldList);
        paragraph.setAlignment(1);
        document.add(paragraph);
        paragraph = new Paragraph("   ");
        document.add(paragraph);

        PdfPTable tablel = new PdfPTable(5);
        tablel.setWidthPercentage(100.0F);//Height and width

        PdfPCell cel1 = new PdfPCell(new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_installations), baseFontBold));
        PdfPCell cel2 = new PdfPCell(new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_description), baseFontBold));
        PdfPCell cel3 = new PdfPCell(new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_conformity), baseFontBold));
        PdfPCell cel4 = new PdfPCell(new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_note), baseFontBold));
        PdfPCell cel5 = new PdfPCell(new Paragraph(context.getResources().getString(R.string.label_paragraph_pdf_photo), baseFontBold));

        tablel.addCell(cel1);
        tablel.addCell(cel2);
        tablel.addCell(cel3);
        tablel.addCell(cel4);
        tablel.addCell(cel5);

        addListItems(paramRepo, listTitle, tablel);
        document.add(tablel);

        document.close();
        return localFile2;
    }

    private void addListItems(ReportItems paramRepo, List listTitle, PdfPTable pTable) throws BadElementException, IOException {
        PdfPCell celTitle;
        PdfPCell celDescription;
        PdfPCell celRadio;
        PdfPCell celNote;
        PdfPCell celImage;
        try {
            JSONArray arrayL = new JSONArray(paramRepo.getListJson());
            for (int i = 0; i < arrayL.length(); i++) {
                JSONObject objTitle = arrayL.getJSONObject(i);
                String title = objTitle.getString(ReportConstants.ITEM.TITLE);

                JSONObject objDescription = arrayL.getJSONObject(i);
                String description = objDescription.getString(ReportConstants.ITEM.DESCRIPTION);

                JSONObject objRadio = arrayL.getJSONObject(i);
                String radio = objRadio.getString(ReportConstants.ITEM.CONFORMITY);

                JSONObject objNote = arrayL.getJSONObject(i);
                String notes = objNote.getString(ReportConstants.ITEM.NOTE);
                Log.d("Notes", notes);

                JSONObject objImage = arrayL.getJSONObject(i);
                String image = objImage.getString(ReportConstants.ITEM.PHOTO);
                Log.d("PDFImage", image);

                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Log.i("log", "PDFImage3: " + decodedByte + " Image");

                Image image1 = Image.getInstance(decodedString);
                Log.i("log", "PDFImage4: " + image1 + " Image");

                listTitle.add(title);
                celTitle = new PdfPCell(new Paragraph(title));
                celDescription = new PdfPCell(new Paragraph(description));
                celRadio = new PdfPCell(new Paragraph(radio));
                celNote = new PdfPCell(new Paragraph(notes));
                celImage = new PdfPCell(new Paragraph(image));
                celImage.setImage(image1);
                celImage.setPadding(5f);

                pTable.addCell(celTitle);
                pTable.addCell(celDescription);
                pTable.addCell(celRadio);
                pTable.addCell(celNote);
                pTable.addCell(celImage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static PdfPCell createImageCell(Image paramImage) {
        PdfPCell localPdfPCell = new PdfPCell(paramImage, true);
        localPdfPCell.setBorder(0);
        return localPdfPCell;
    }

    public static PdfPCell createImageCll(String paramString)
            throws DocumentException, IOException {
        return createImageCell(Image.getInstance(paramString));
    }

    //Right side
    public static PdfPCell createTextCell(Paragraph paramParagraph) {
        paramParagraph.setAlignment(0);
        paramParagraph.setIndentationLeft(lineSpaceSmall);
        PdfPCell localPdfPCell = new PdfPCell();
        localPdfPCell.addElement(paramParagraph);
        localPdfPCell.setVerticalAlignment(4);
        localPdfPCell.setBorder(0);
        return localPdfPCell;
    }
}
