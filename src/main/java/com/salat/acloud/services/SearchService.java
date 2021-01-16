package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.parsers.DOCXParser;
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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserService userService;
    private final UserFileService userFileService;

    public List<UserFile> getFilesByQuery(String queryStr) throws FileNotFoundException {
        try {
            User currentUser = userService.getUserFromContext();

            List<File> filesForIndexing = currentUser.getUserFiles().stream()
                    .map(UserFile::makeFile)
                    .collect(Collectors.toList()); // todo this shit clogs memory
            List<File> txtFiles = filesForIndexing.stream()
                    .filter(file -> userFileService.getContentType(file).equals("txt"))
                    .collect(Collectors.toList());
            List<File> docxFiles = filesForIndexing.stream()
                    .filter(file -> userFileService.getContentType(file).equals("docx"))
                    .collect(Collectors.toList());

            List<Document> documents = new ArrayList<>();
            documents.addAll(TXTParser.parse(txtFiles));
            documents.addAll(DOCXParser.parse(docxFiles));

            Analyzer analyzer = new EnglishAnalyzer();
//            Analyzer analyzerRussian = new RussianAnalyzer(); todo
//            Directory directory = new NIOFSDirectory(Paths.get("/" + currentUser.getId()));
            Directory directory = new RAMDirectory();
            updateIndex(documents, analyzer, directory);

            QueryParser parser = new QueryParser("content", analyzer);
            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(directory));

            Query query = parser.parse(queryStr);

            TopFieldDocs search = searcher.search(query, filesForIndexing.size(), Sort.RELEVANCE);
            System.out.println(search.totalHits);

            List<UserFile> results = new ArrayList<>();
            for (ScoreDoc hit : search.scoreDocs) {
                System.out.println(hit);
//                System.out.println(docsFromTxts.get(hit.doc));
                String resultFilename = documents.get(hit.doc).getField("filename").stringValue();
                System.out.println(resultFilename);
                results.add(currentUser.getUserFiles().stream()
                        .filter(file -> file.getFilename().equals(resultFilename))
                        .collect(Collectors.toList())
                        .get(0));
                System.out.println();
            }

            directory.close();

            return results;
        } catch (IOException | ParseException | IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            throw new FileNotFoundException();
        }
    }

    public void updateIndex(List<Document> documents, Analyzer analyzer, Directory directory) throws IOException {
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        for (Document document : documents) {
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }
}
