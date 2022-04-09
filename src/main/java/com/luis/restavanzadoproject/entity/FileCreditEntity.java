package com.luis.restavanzadoproject.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Entity
@Table(name = "FILE_CREDIT")
public class FileCreditEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FILECREDIT_SEQUENCE")
    @SequenceGenerator(name = "FILECREDIT_SEQUENCE", sequenceName = "FILECREDIT_SEQUENCE", allocationSize=1)
    private Long id;

    @Column(name = "ID_CREDIT")
    private Long idCredit;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    public FileCreditEntity(Long id, Long idCredit, String name, LocalDateTime createAt) {
        this.id = id;
        this.idCredit = idCredit;
        this.name = name;
        this.createAt = createAt;
    }

    public FileCreditEntity() {
    }

    @PrePersist
    private void createAt(){
        this.createAt = LocalDateTime.now();
    }


}
