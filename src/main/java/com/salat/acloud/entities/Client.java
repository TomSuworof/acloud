package com.salat.acloud.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "t_client")
public class Client {

    @Id
    private Long clientId;

    @Column
    private String clientName;

    @Column
    private String clientSecret;
}
