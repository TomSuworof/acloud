package com.salat.acloud.threads;

import com.salat.acloud.entities.User;
import com.salat.acloud.services.SearchService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.util.concurrent.Phaser;

@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageThreadDeleter extends LanguageThread {

    private Document document;
    private SearchService searchService;
    private User user;

    public LanguageThreadDeleter(
            Document document,
            SearchService searchService,
            User user,
            Phaser phaser) {
        super(phaser);
        this.document = document;
        this.searchService = searchService;
        this.user = user;
        this.phaser.register();
    }

    @Override
    public void run() {
//        try {
//
//            this.searchService.deleteFromIndex(
//                    this.document,
//                    this.analyzer,
//                    this.user
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        this.phaser.arriveAndAwaitAdvance();
        this.phaser.arriveAndDeregister();
    }

    @Override
    public LanguageThread clone() {
        return new LanguageThreadDeleter(
                this.document,
                this.searchService,
                this.user,
                this.phaser
        );
    }
}