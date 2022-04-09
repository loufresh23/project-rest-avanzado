package com.luis.restavanzadoproject.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "TYPE_CREDIT")
public class TypeCreditEntity {

    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "DESCRIPTION")
    private String description;

}
