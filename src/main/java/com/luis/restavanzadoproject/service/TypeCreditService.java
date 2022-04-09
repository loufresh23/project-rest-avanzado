package com.luis.restavanzadoproject.service;

import com.luis.restavanzadoproject.entity.CustomerEntity;
import com.luis.restavanzadoproject.entity.TypeCreditEntity;

import java.util.List;

public interface TypeCreditService {

    List<TypeCreditEntity> findAll();
    TypeCreditEntity findById(Long id);
    void truncateReadData(List<TypeCreditEntity> lst);

}
