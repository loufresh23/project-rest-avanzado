package com.luis.restavanzadoproject.repository;

import com.luis.restavanzadoproject.entity.TypeCreditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TypeCreditRepository extends JpaRepository<TypeCreditEntity, Long> {

    @Modifying
    @Query(nativeQuery=true,value="truncate table TYPE_CREDIT")
    void truncateTabla();

}
