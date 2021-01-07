package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final UserService userService;

    public List<UserFile> getFilesByQuery(String query) throws FileNotFoundException {
        try {
            User currentUser = userService.getUserFromContext();
            List<File> filesForIndexing = currentUser.getUserFiles().stream()
                    .map(UserFile::makeFile)
                    .collect(Collectors.toList());
            updateIndex(null, currentUser.getId()); // todo replace null
            return null;
        } catch (IOException ioException) {
            throw new FileNotFoundException();
        }
    }

    public void updateIndex(List<Document> documents, Long id) throws IOException {
        Analyzer analyzer = new EnglishAnalyzer();
        Directory directory = new NIOFSDirectory(Paths.get("/" + id));
        IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
        for (Document document : documents) {
            indexWriter.addDocument(document);
        }
        indexWriter.close();
    }
}
