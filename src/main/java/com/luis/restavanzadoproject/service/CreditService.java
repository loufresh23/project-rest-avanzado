package com.luis.restavanzadoproject.service;

import com.luis.restavanzadoproject.entity.CreditEntity;
import java.util.List;

public interface CreditService {

    List<CreditEntity> findAll();
    CreditEntity findById(Long id);
    void save(CreditEntity creditEntity);

}
