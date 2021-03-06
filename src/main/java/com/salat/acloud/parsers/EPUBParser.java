package com.salat.acloud.parsers;

import com.github.mertakdut.Reader;
import com.github.mertakdut.exception.OutOfPagesException;
import com.github.mertakdut.exception.ReadingException;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;


import java.io.File;

public class EPUBParser {
    public static Document parse(File file) {
        Document document = new Document();
        document.add(new TextField("filename", file.getName(), Field.Store.YES));
        StringBuilder content = new StringBuilder();
        try {
            Reader reader = new Reader();
            reader.setMaxContentPerSection(1000);
            reader.setIsIncludingTextContent(true);
            reader.setFullContent(file.getPath());

            for (int i = 0; ; i++) {
                content.append(reader.readSection(i).getSectionTextContent());
            }
        } catch (ReadingException | OutOfPagesException e) {
            e.printStackTrace();
        } finally {
            document.add(new TextField("content", content.toString(), Field.Store.YES));
        }
        return document;
    }
}
