package com.luis.restavanzadoproject.repository;

import com.luis.restavanzadoproject.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    @Modifying
    @Query(nativeQuery=true,value="truncate table CUSTOMER")
    void truncateTabla();

}
