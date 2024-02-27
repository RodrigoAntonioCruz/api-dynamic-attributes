package com.example.repositories;

import com.example.domains.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> findByKeyword(String keyword, Pageable pageable);

}
