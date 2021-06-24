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

    @Column
    private boolean encrypted;

    public UserFile(MultipartFile file, String author) {
        this.author = author;
        this.filename = file.getOriginalFilename();
        this.id = (long) (this.author + this.filename).hashCode();
        try {
            System.out.println("Plain: " + file.getBytes()[0]);
            this.content = EncryptionService.encode(file.getBytes());
            System.out.println("Encrypted: " + this.content[0]);
//            this.content = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.encrypted = true;
        this.canBeDownloadedPublicly = false;
    }

    public File makeFile() {
        File file = new File(this.filename);
        try (OutputStream outputStream = new FileOutputStream(file)) {
            if (this.encrypted) {
                System.out.println("Encrypted: " + this.content[0]);
                this.content = EncryptionService.decode(this.content);
                System.out.println("Decrypted: " + this.content[0]);
            }
            outputStream.write(this.content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    // Utilities
    public String getExtension() {
        String[] splitted = this.filename.split("\\.");
        return splitted[splitted.length - 1];
    }
}
