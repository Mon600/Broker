package com.shastage.worker.datasource.repository.jpa;

import com.shastage.worker.datasource.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    @Query(
            value = "SELECT * FROM products ORDER BY random() LIMIT 1",
            nativeQuery = true
    )
    Optional<ProductEntity> findRandomProduct();
}
