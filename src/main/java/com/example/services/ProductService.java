package com.example.services;


import com.example.domains.Product;
import com.example.domains.dto.ProductDTO;
import com.example.exceptions.GenericException;
import com.example.exceptions.NotFoundException;
import com.example.repositories.ProductRepository;
import com.example.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ProductService {

    private final ModelMapper mapper;

    private final ProductRepository productRepository;

    public ProductDTO create(ProductDTO productDTO) {

        var product = mapper.map(productDTO, Product.class);

        product.setCreatedAt(LocalDateTime.now());

        return saveProduct(null, product);
    }

    public ProductDTO patch(String id, ProductDTO productDTO) {

        Product updateProduct = findProduct(id);
        ProductDTO product = ProductDTO.builder().build();

        if (!StringUtils.isEmpty(productDTO.getDescription())) {

            updateProduct.setDescription(productDTO.getDescription());
            product = saveProduct(id, updateProduct);
        } else {
            throw new GenericException(Constants.DESCRIPTION_NOT_NULL);
        }

        if (productDTO.getAttributes() != null) {

            var attributes = updateProduct.updateDynamicAttributes(productDTO.getAttributes());
            updateProduct.setAttributes(attributes);
            product = saveProduct(id, updateProduct);
        }

        return product;
    }

    public ProductDTO update(String id, ProductDTO productDTO) {

        var updateProduct = findProduct(id);

        updateProduct.setDescription(productDTO.getDescription());
        updateProduct.setAttributes(productDTO.getAttributes());

        return saveProduct(id, updateProduct);
    }

    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(product -> mapper.map(product, ProductDTO.class));
    }

    public ProductDTO findById(String id) {

        var product = findProduct(id);
        return mapper.map(product, ProductDTO.class);
    }

    public void delete(String id) {
        productRepository.deleteById(findProduct(id).getId());
    }

    private Product findProduct(String id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.NOT_FOUND));
    }

    private ProductDTO saveProduct(String id, Product product) {
        product.setId(id);
        product.setUpdatedAt(LocalDateTime.now());

        var productUpdated = productRepository.save(product);
        return mapper.map(productUpdated, ProductDTO.class);
    }
}