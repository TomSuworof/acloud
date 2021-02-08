package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.parsers.DOCXParser;
import com.salat.acloud.parsers.PDFParser;
import com.salat.acloud.parsers.TXTParser;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
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
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserService userService;
    private final UserFileService userFileService;

    public List<String> getSuggestionsByQuery(String queryStr) throws IOException {
//        User currentUser = userService.getUserFromContext();

        Set<String> suggestions = new HashSet<>(getSuggestionsByQueryAndAnalyzerOfUser(queryStr));

        return new ArrayList<>(suggestions);
    }

    private List<String> getSuggestionsByQueryAndAnalyzerOfUser(String queryStr) throws IOException {
        Directory directory = new RAMDirectory();
//        Directory directory = new NIOFSDirectory(Paths.get("/" + currentUser.getId()));
        SpellChecker spellChecker = new SpellChecker(directory);
        spellChecker.indexDictionary(new LuceneDictionary(DirectoryReader.open(directory), "content"), new IndexWriterConfig(), true);
        directory.close();
        return Arrays.asList(spellChecker.suggestSimilar(queryStr, 10));
    }

    public List<UserFile> getFilesByQuery(String queryStr) throws FileNotFoundException {
        User currentUser = userService.getUserFromContext();

        List<Analyzer> analyzers = Arrays.asList(new EnglishAnalyzer(), new RussianAnalyzer());

        Set<UserFile> results = new HashSet<>();
        for (Analyzer analyzer : analyzers) {
            results.addAll(getFilesByQueryAndAnalyzerOfUser(queryStr, analyzer, currentUser));
        }
        return new ArrayList<>(results);
    }

    private List<UserFile> getFilesByQueryAndAnalyzerOfUser(String queryStr, Analyzer analyzer, User currentUser) throws FileNotFoundException {
        try {
            List<File> filesForIndexing = currentUser.getUserFiles().stream()
                    .map(UserFile::makeFile)
                    .collect(Collectors.toList()); // todo this shit clogs memory
            List<File> txtFiles = filesForIndexing.stream()
                    .filter(file -> userFileService.getExtension(file).equals("txt"))
                    .collect(Collectors.toList());
            List<File> docxFiles = filesForIndexing.stream()
                    .filter(file -> userFileService.getExtension(file).equals("docx"))
                    .collect(Collectors.toList());
            List<File> pdfFiles = filesForIndexing.stream()
                    .filter(file -> userFileService.getExtension(file).equals("pdf"))
                    .collect(Collectors.toList());

            List<Document> documents = new ArrayList<>();
            documents.addAll(TXTParser.parse(txtFiles));
            documents.addAll(DOCXParser.parse(docxFiles));
            documents.addAll(PDFParser.parse(pdfFiles));

            Directory directory = new RAMDirectory();
//            Directory directory = new NIOFSDirectory(Paths.get("/" + currentUser.getId()));

            updateIndex(documents, analyzer, directory);

            QueryParser parser = new QueryParser("content", analyzer);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));
            Query query = parser.parse(queryStr);
            TopFieldDocs search = searcher.search(query, filesForIndexing.size(), Sort.RELEVANCE);

            List<UserFile> results = new ArrayList<>();
            for (ScoreDoc hit : search.scoreDocs) {
                System.out.println(hit);
                String resultFilename = documents.get(hit.doc).getField("filename").stringValue();
                System.out.println(resultFilename);
                results.add(currentUser.getUserFiles().stream()
                        .filter(file -> file.getFilename().equals(resultFilename))
                        .collect(Collectors.toList())
                        .get(0));
            }

            directory.close();

            return results;
        } catch (IOException | ParseException | IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            throw new FileNotFoundException();
        }
    }

    private void updateIndex(List<Document> documents, Analyzer analyzer, Directory directory) throws IOException {
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        for (Document document : documents) {
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }
}
