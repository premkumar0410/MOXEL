package com.moxel.store.repository;

import com.moxel.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
           select distinct p from Product p
           join fetch p.category
           left join fetch p.variants
           where p.active = true
           order by p.createdAt desc
           """)
    List<Product> findAllActiveWithDetails();

    @Query("""
           select p from Product p
           join fetch p.category
           left join fetch p.variants
           where p.slug = :slug and p.active = true
           """)
    Optional<Product> findActiveBySlugWithDetails(@Param("slug") String slug);
}