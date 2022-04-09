package com.luis.restavanzadoproject.service.impl;

import com.luis.restavanzadoproject.entity.CreditEntity;
import com.luis.restavanzadoproject.repository.CreditRepository;
import com.luis.restavanzadoproject.service.CreditService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {

    private final CreditRepository repository;

    public CreditServiceImpl(CreditRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<CreditEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public CreditEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public void save(CreditEntity creditEntity) {
        repository.save(creditEntity);
    }
}
