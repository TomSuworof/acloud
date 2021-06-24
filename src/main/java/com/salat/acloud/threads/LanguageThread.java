package com.salat.acloud.threads;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.lucene.analysis.Analyzer;

import java.util.concurrent.Phaser;

@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class LanguageThread extends Thread implements Cloneable {
    protected Analyzer analyzer;
    protected Phaser phaser;

    public LanguageThread(Phaser phaser) {
        this.phaser = phaser;
    }

    @Override
    public abstract LanguageThread clone() throws CloneNotSupportedException;
}
