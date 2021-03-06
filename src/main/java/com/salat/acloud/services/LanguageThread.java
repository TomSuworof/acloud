package com.salat.acloud.services;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.lucene.analysis.Analyzer;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageThread extends Thread {
    private List<UserFile> userFiles;

    private SearchService searchService;
    private Phaser phaser;

    private String queryStr;
    private Analyzer analyzer;
    private User currentUser;

    LanguageThread(SearchService searchService,
                   String queryStr,
                   Analyzer analyzer,
                   User currentUser,
                   Phaser phaser) {
        this.searchService = searchService;
        this.queryStr = queryStr;
        this.analyzer = analyzer;
        this.currentUser = currentUser;
        this.phaser = phaser;
        this.phaser.register();
    }

    @Override
    public void run() {
        try {
            this.userFiles = new ArrayList<>();
            this.userFiles.addAll(searchService.getFilesByQueryAndAnalyzerOfUser(
                    this.queryStr,
                    this.analyzer,
                    this.currentUser
            ));
        } catch (FileNotFoundException noFile) {
            noFile.printStackTrace();
        }
        this.phaser.arriveAndAwaitAdvance();
        this.phaser.arriveAndDeregister();
    }
}
