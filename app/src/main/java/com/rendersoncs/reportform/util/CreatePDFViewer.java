package com.rendersoncs.reportform.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

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
import com.rendersoncs.reportform.itens.ReportItems;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

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
        Log.i("PDF", "Gerar PDF!!!! " + arrayOfObject + " Nome arrayOfObject");
        //arrayOfObject[1] = date;
        String str = FilenameUtils.normalize(String.format("Relatorio-%s-%s.pdf", arrayOfObject));*/

        String str = FilenameUtils.normalize(String.format("Relatorio-%s-%s.pdf", paramRepo.getCompany(), paramRepo.getDate()));
        Log.i("PDF", "Gerar PDF!!!! " + str + " Nome Arquivo");

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
        table.setWidthPercentage(100.0F);//altura e largura
        table.setWidths(new int[]{1, 2});//setando a altura e largura
        table.addCell(createImageCell(image));

        Paragraph paragraph = new Paragraph(new Chunk("Auditoria Técnica - Segurança Alimentar", baseFont));
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
        paragraph = new Paragraph(new Chunk("Nome da Empresa: ", baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getCompany(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        //New paragraph E-mail
        paragraph = new Paragraph(new Chunk("E-mail da empresa: ", baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getEmail(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        //New paragraph Date
        paragraph = new Paragraph(new Chunk("Data da Vistoria: ", baseFontBold));
        paragraph.setSpacingAfter(lineSpaceSmall);
        paragraph.setSpacingBefore(lineSpaceSmall);
        paragraph.add(new Chunk(paramRepo.getDate(), baseFont));
        paragraph.setAlignment(0);
        document.add(paragraph);

        // Create Table Check-List
        paragraph = new Paragraph("Check List", baseFontBoldList);
        paragraph.setAlignment(1);
        document.add(paragraph);
        paragraph = new Paragraph("   ");
        document.add(paragraph);

        PdfPTable tablel = new PdfPTable(2);
        tablel.setWidthPercentage(100.0F);//altura e largura

        PdfPCell cel1 = new PdfPCell(new Paragraph("INSTALAÇÕES FÍSICAS ", baseFontBold));
        PdfPCell cel2 = new PdfPCell(new Paragraph("Avaliação ", baseFontBold));

        tablel.addCell(cel1);
        tablel.addCell(cel2);

        try {
            JSONArray arrayL = new JSONArray(paramRepo.getListJson());
            for (int i = 0; i < arrayL.length(); i++) {
                JSONObject obj = arrayL.getJSONObject(i);
                String selected = obj.getString("title_list");

                JSONObject obj2 = arrayL.getJSONObject(i);
                String selected2 = obj2.getString("radio_tx");

                listTitle.add(selected);
                cel1 = new PdfPCell(new Paragraph(selected));
                cel2 = new PdfPCell(new Paragraph(selected2));

                tablel.addCell(cel1);
                tablel.addCell(cel2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        document.add(tablel);

        document.close();
        return localFile2;
    }

    private static PdfPCell createImageCell(Image paramImage)
            throws DocumentException {
        PdfPCell localPdfPCell = new PdfPCell(paramImage, true);
        localPdfPCell.setBorder(0);
        return localPdfPCell;
    }

    public static PdfPCell createImageCll(String paramString)
            throws DocumentException, IOException {
        return createImageCell(Image.getInstance(paramString));
    }

    //Lado direito
    public static PdfPCell createTextCell(Paragraph paramParagraph)
            throws DocumentException {
        paramParagraph.setAlignment(0);
        paramParagraph.setIndentationLeft(lineSpaceSmall);
        PdfPCell localPdfPCell = new PdfPCell();
        localPdfPCell.addElement(paramParagraph);
        localPdfPCell.setVerticalAlignment(4);
        localPdfPCell.setBorder(0);
        return localPdfPCell;
    }
}