package com.salat.acloud.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFParser {
    public static List<Document> parse(List<File> files) {
        List<Document> translatedFiles = new ArrayList<>();
        for (File file : files) {
            Document document = new Document();
            document.add(new TextField("filename", file.getName(), Field.Store.YES));
            try {
                PDDocument pdfDoc = PDDocument.load(file);
                if (!pdfDoc.isEncrypted()) {
                    String content = new PDFTextStripper().getText(pdfDoc);
                    document.add(new TextField("content", content, Field.Store.YES));
                    translatedFiles.add(document);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return translatedFiles;
    }
}
