package com.salat.acloud.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class DashboardService {

    private static String getQuery() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "lucene";
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new EnglishAnalyzer();
        Directory directory = new RAMDirectory();
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));

        Document simpleDoc = new Document();
        simpleDoc.add(new TextField("title", "Simple doc", Field.Store.YES));
        simpleDoc.add(new TextField("content", "Here is simplest document, that could contain any data", Field.Store.YES));

        Document theBook = new Document();
        theBook.add(new TextField("title", "The book about the Princess", Field.Store.YES));
        theBook.add(new TextField("content", "In a far, distant land in a not so simple tower...", Field.Store.YES));

        List<Document> docs = Arrays.asList(simpleDoc, theBook);
        for (Document doc : docs) {
            indexWriter.addDocument(doc);
        }
        indexWriter.close();

        QueryParser parser = new QueryParser("content", analyzer);
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));

        String queryStr = getQuery();

        SpellChecker spellChecker = new SpellChecker(directory);
        spellChecker.indexDictionary(new LuceneDictionary(DirectoryReader.open(directory), "content"), new IndexWriterConfig(), true);
        List<String> suggestions = Arrays.asList(spellChecker.suggestSimilar(queryStr, 10));
        System.out.println("Do you mean?..");
        suggestions.forEach(System.out::println);

        Query query = parser.parse(queryStr);
        TopFieldDocs search = searcher.search(query, 10, Sort.RELEVANCE);
        System.out.println(search.totalHits);

        for (ScoreDoc hit : search.scoreDocs) {
            System.out.println(hit);
            System.out.println(docs.get(hit.doc));
            System.out.println();
        }

        directory.close();
    }
}
