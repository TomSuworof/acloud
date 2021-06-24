package com.salat.acloud.parsers;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import java.io.*;
import java.util.stream.Collectors;

public class TXTParser {
    public static Document parse(File file) {
        Document document = new Document();
        document.add(new TextField("filename", file.getName(), Field.Store.YES));
        String contentBuilder = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            contentBuilder = reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.add(new TextField("content", contentBuilder, Field.Store.YES));
        return document;
    }
}
