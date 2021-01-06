package com.salat.acloud.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.*;

@Data
@Entity
@Table(name = "t_file")
@NoArgsConstructor
public class UserFile {

    @Id
    private Long id;

    @Column
    private String author;

    @Column
    private String filename;

    @Column
    private byte[] content;

    public File makeFile() {
        File file = new File(filename);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public UserFile(MultipartFile file, String author) {
        this.author = author;
        this.filename = file.getOriginalFilename();
        try {
            this.content = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.id = (long) (this.author + this.filename).hashCode();
    }
}
