package com.salat.acloud.threads;

import com.salat.acloud.entities.User;
import com.salat.acloud.entities.UserFile;
import com.salat.acloud.services.SearchService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageThreadSearcher extends LanguageThread {
    private List<UserFile> userFiles;

    private String queryStr;
    private SearchService searchService;
    private User user;

    public LanguageThreadSearcher(
            String queryStr,
            SearchService searchService,
            User user,
            Phaser phaser) {
        super(phaser);
        this.queryStr = queryStr;
        this.searchService = searchService;
        this.user = user;
        this.phaser.register();
    }

    @Override
    public void run() {
//        try {
//            this.userFiles = new ArrayList<>();
//            this.userFiles.addAll(this.searchService.getFilesByQueryAndAnalyzerOfUser(
//                    this.queryStr,
//                    this.analyzer,
//                    this.user
//            ));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        this.phaser.arriveAndAwaitAdvance();
        this.phaser.arriveAndDeregister();
    }

    @Override
    public LanguageThread clone() {
        return new LanguageThreadSearcher(
                this.queryStr,
                this.searchService,
                this.user,
                this.phaser
        );
    }
}