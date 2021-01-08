package com.salat.acloud.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DOCXParser {
    public static List<Document> parse(List<File> files) throws IOException {
        List<Document> translatedFiles = new ArrayList<>();
        for (File file : files) {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
            Document document = new Document();
            document.add(new TextField("filename", file.getName(), Field.Store.YES));
            document.add(new TextField("content", extractor.getText(), Field.Store.YES));
            translatedFiles.add(document);
        }
        return translatedFiles;
    }
}
