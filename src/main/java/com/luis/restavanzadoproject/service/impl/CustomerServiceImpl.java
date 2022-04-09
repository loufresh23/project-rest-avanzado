package com.luis.restavanzadoproject.service.impl;

import com.luis.restavanzadoproject.entity.CustomerEntity;
import com.luis.restavanzadoproject.repository.CustomerRepository;
import com.luis.restavanzadoproject.service.CustomerService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    public CustomerServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CustomerEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public CustomerEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void truncateReadData(List<CustomerEntity> lst) {
        this.repository.truncateTabla();
        this.repository.saveAll(lst);
    }

}
