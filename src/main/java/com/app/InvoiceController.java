package com.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private PdfService pdfService;

    public InvoiceController(PdfService pdfService) {
        this.pdfService = pdfService;
    }


    @PostMapping("/generate")
    public ResponseEntity<Resource> generatePdf(@RequestBody Invoice invoice) {
        try {
            // Generate PDF and get file path
            String pdfPath = pdfService.generatePdf(invoice);
            Path path = Paths.get(pdfPath);

            // Create resource from the file path
            Resource resource = new FileSystemResource(path);

            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName().toString());
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}

