package com.luis.restavanzadoproject.service.impl;

import com.luis.restavanzadoproject.entity.TypeCreditEntity;
import com.luis.restavanzadoproject.repository.TypeCreditRepository;
import com.luis.restavanzadoproject.service.TypeCreditService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class TypeCreditServiceImpl implements TypeCreditService {

    private final TypeCreditRepository repository;

    public TypeCreditServiceImpl(TypeCreditRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<TypeCreditEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public TypeCreditEntity findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void truncateReadData(List<TypeCreditEntity> lst) {
        this.repository.truncateTabla();
        this.repository.saveAll(lst);
    }

}
