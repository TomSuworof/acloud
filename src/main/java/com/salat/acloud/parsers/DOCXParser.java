package com.salat.acloud.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DOCXParser {
    public static Document parse(File file) throws IOException {
        XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
        XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
        Document document = new Document();
        document.add(new TextField("filename", file.getName(), Field.Store.YES));
        document.add(new TextField("content", extractor.getText(), Field.Store.YES));
        return document;
    }
}
