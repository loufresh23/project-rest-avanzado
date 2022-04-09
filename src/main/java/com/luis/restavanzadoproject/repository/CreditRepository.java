package com.luis.restavanzadoproject.repository;

import com.luis.restavanzadoproject.entity.CreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<CreditEntity, Long> {
}
