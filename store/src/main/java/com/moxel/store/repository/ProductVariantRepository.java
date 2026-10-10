package com.moxel.store.repository;

import com.moxel.store.model.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    @Query("""
           select v from ProductVariant v
           join fetch v.product p
           where v.id = :id and p.active = true
           """)
    Optional<ProductVariant> findActiveById(@Param("id") Long id);

    @Query("""
           select v from ProductVariant v
           join fetch v.product p
           where v.id in (:ids) and p.active = true
           """)
    List<ProductVariant> findActiveByIdIn(@Param("ids") Collection<Long> ids);
}