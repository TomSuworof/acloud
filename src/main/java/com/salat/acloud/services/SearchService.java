package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.parsers.*;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserService userService;

    private Document convertUserFileToDocument(UserFile userFile) throws IOException {
        File file = userFile.makeFile();
        return switch (userFile.getExtension()) {
            case "txt"  -> TXTParser.parse(file);
            case "docx" -> DOCXParser.parse(file);
            case "pdf"  -> PDFParser.parse(file);
            case "epub" -> EPUBParser.parse(file);
            default     -> throw new IllegalStateException("Unexpected value: " + userFile.getExtension());
        };
    }

    // Suggestion engine
    public List<String> getSuggestionsByQuery(String queryStr) throws IOException {
        User currentUser = userService.getUserFromContext();

        Directory directory = new NIOFSDirectory(Paths.get("app/indexes/" + currentUser.getId()));
        SpellChecker spellChecker = new SpellChecker(directory);
        spellChecker.indexDictionary(new LuceneDictionary(DirectoryReader.open(directory), "content"), new IndexWriterConfig(), true);
        directory.close();
        return Arrays.asList(spellChecker.suggestSimilar(queryStr, 10));
    }


    // Search files in index
    public List<UserFile> getFilesByQuery(String queryStr, User user) throws IOException, CloneNotSupportedException {
        List<Analyzer> analyzers = Arrays.asList(new EnglishAnalyzer(), new RussianAnalyzer());
        Set<UserFile> results = new HashSet<>();

        for (Analyzer analyzer : analyzers) {
            results.addAll(getFilesByQuery(queryStr, analyzer, user));
        }

        return new ArrayList<>(results);
    }

    private List<UserFile> getFilesByQuery(String queryStr, Analyzer analyzer, User user) throws FileNotFoundException {
        try {
            Directory directory = new NIOFSDirectory(Paths.get("app/indexes/" + user.getId()));

            QueryParser parser = new QueryParser("content", analyzer);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
            Query query = parser.parse(queryStr);
            TopFieldDocs search = searcher.search(query, user.getUserFiles().size(), Sort.RELEVANCE);

            List<UserFile> results = new ArrayList<>();

            Arrays.stream(search.scoreDocs).forEach(System.out::println);
            System.out.println("Files:");

            for (ScoreDoc hit : search.scoreDocs) {
                try {
                    String resultFilename = searcher.doc(hit.doc).getField("filename").stringValue();
                    System.out.println(resultFilename);
                    results.add(user.getUserFiles().stream()
                            .filter(file -> file.getFilename().equals(resultFilename))
                            .collect(Collectors.toList())
                            .get(0));
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }

            directory.close();

            return results;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new FileNotFoundException();
        }
    }


    // Upload new files to index
    public boolean loadFileToIndex(UserFile userFile, User user) {
        try {
            Document document = this.convertUserFileToDocument(userFile);
            List<Analyzer> analyzers = Arrays.asList(new EnglishAnalyzer(), new RussianAnalyzer());
            for (Analyzer analyzer : analyzers) {
                loadFileToIndex(document, analyzer, user);
            }

        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void loadFileToIndex(Document document, Analyzer analyzer, User user) throws IOException {
        Directory directory = new NIOFSDirectory(Paths.get("app/indexes/" + user.getId()));
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        indexWriter.addDocument(document);
        indexWriter.close();
    }


    // Delete file from index
    public boolean deleteFileFromIndex(UserFile userFile, User user) {
        try {
            Document document = this.convertUserFileToDocument(userFile);
            List<Analyzer> analyzers = Arrays.asList(new EnglishAnalyzer(), new RussianAnalyzer());
            for (Analyzer analyzer : analyzers) {
                deleteFileFromIndex(document, analyzer, user);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void deleteFileFromIndex(Document document, Analyzer analyzer, User user) throws IOException {
        Directory directory = new NIOFSDirectory(Paths.get("app/indexes/" + user.getId()));
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        indexWriter.deleteDocuments(new Term(document.get("filename")));
        indexWriter.close();
    }
}
