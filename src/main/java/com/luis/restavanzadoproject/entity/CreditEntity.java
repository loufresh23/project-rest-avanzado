package com.luis.restavanzadoproject.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CREDIT")
public class CreditEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CREDIT_SEQUENCE")
    @SequenceGenerator(name = "CREDIT_SEQUENCE", sequenceName = "CREDIT_SEQUENCE", allocationSize=1)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TYPECREDIT_ID", nullable = false)
    private TypeCreditEntity typeCredit;

    @Column(name = "AMOUNT")
    private Double amount;

    @Column(name = "DUES")
    private int dues;

    @Column(name = "INTERESTS")
    private Double interests;

    @Column(name = "REASON")
    private String reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "DESCRIPTION_EVALUATION")
    private String descriptionEvaluation;

    @Column(name = "CREATE_AT")
    private LocalDateTime createAt;

    @PrePersist
    private void createAt(){
        this.createAt = LocalDateTime.now();
    }

}