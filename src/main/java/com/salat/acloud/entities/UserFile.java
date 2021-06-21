package com.salat.acloud.entities;

import com.salat.acloud.services.EncryptionService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
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

    @Column
    private boolean canBeDownloadedPublicly;

    public UserFile(MultipartFile file, String author) {
        this.author = author;
        this.filename = file.getOriginalFilename();
        try {
//            this.content = EncryptionService.encode(file.getBytes());
            this.content = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.canBeDownloadedPublicly = false;
        this.id = (long) (this.author + this.filename).hashCode();
    }

    public File makeFile() {
        File file = new File(this.filename);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(this.content);
//            this.content = EncryptionService.decode(this.content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
