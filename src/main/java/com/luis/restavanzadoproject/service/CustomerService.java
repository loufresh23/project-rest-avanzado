package com.luis.restavanzadoproject.service;

import com.luis.restavanzadoproject.entity.CustomerEntity;

import java.util.List;

public interface CustomerService {

    List<CustomerEntity> findAll();
    CustomerEntity findById(Long id);
    void truncateReadData(List<CustomerEntity> lst);
}
