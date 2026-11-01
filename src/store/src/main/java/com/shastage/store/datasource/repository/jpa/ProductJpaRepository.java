package com.shastage.store.datasource.repository.jpa;

import com.shastage.store.datasource.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, UUID> {
    @Query(nativeQuery = true, value = "SELECT image_id FROM products WHERE id = :id")
    UUID findImageIdByProductId(UUID id);
}
