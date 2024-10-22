package com.app;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class PdfService {

    private static final String PDF_STORAGE_PATH = "generated_pdfs/";

    public String generatePdf(Invoice invoice) throws IOException {
        String pdfFileName = PDF_STORAGE_PATH + "invoice_" + System.currentTimeMillis() + ".pdf";
        File file = new File(pdfFileName);
        file.getParentFile().mkdirs(); // Ensure the directory exists

        try (PdfWriter writer = new PdfWriter(pdfFileName)) {
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Add Seller and Buyer Information
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            document.add(new Paragraph("Seller:").setFont(font).setBold());
            document.add(new Paragraph(invoice.getSeller())
                    .add("\n" + invoice.getSellerAddress())
                    .add("\nGSTIN: " + invoice.getSellerGstin()));

            document.add(new Paragraph("\nBuyer:").setFont(font).setBold());
            document.add(new Paragraph(invoice.getBuyer())
                    .add("\n" + invoice.getBuyerAddress())
                    .add("\nGSTIN: " + invoice.getBuyerGstin()));

            // Add a Table for Invoice Items
            float[] columnWidths = {200f, 100f, 100f, 100f};
            Table table = new Table(columnWidths);
            table.addCell(new Cell().add(new Paragraph("Item").setFont(font).setBold()));
            table.addCell(new Cell().add(new Paragraph("Quantity").setFont(font).setBold()));
            table.addCell(new Cell().add(new Paragraph("Rate").setFont(font).setBold()));
            table.addCell(new Cell().add(new Paragraph("Amount").setFont(font).setBold()));

            // Populate table with item data
            for (Item item : invoice.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount())).setFont(font)));
            }

            document.add(new Paragraph("\n"));
            document.add(table);

            document.close();
        } catch (FileNotFoundException e) {
            throw new IOException("File not found: " + pdfFileName, e);
        }

        return pdfFileName;
    }
}
