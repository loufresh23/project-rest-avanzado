package com.luis.restavanzadoproject.service;

import com.luis.restavanzadoproject.entity.CreditEntity;
import com.luis.restavanzadoproject.entity.FileCreditEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FileCreditService {

    FileCreditEntity findById(Long id);

    void save(FileCreditEntity fileCredit, CreditEntity credit, MultipartFile file);

}
