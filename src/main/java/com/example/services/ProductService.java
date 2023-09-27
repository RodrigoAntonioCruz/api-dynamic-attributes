package com.example.services;


import com.example.domains.Product;
import com.example.domains.dto.ProductDTO;
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
        return saveProduct(null, product);
    }

    public ProductDTO patch(String id, ProductDTO productDTO) {
        Product patchProduct = findProduct(id);

        if (!StringUtils.isEmpty(productDTO.getDescription())) {
            patchProduct.setDescription(productDTO.getDescription());
        }

        if (productDTO.getAttributes() != null) {
            var attributes = patchProduct.patchAttributes(productDTO.getAttributes());
            patchProduct.setAttributes(attributes);
        }

        return saveProduct(id, patchProduct);
    }


    public ProductDTO update(String id, ProductDTO productDTO) {
        var updateProduct = findProduct(id);
        var attributes = updateProduct.updateAttributes(productDTO.getAttributes());

        updateProduct.setDescription(productDTO.getDescription());
        updateProduct.setAttributes(attributes);

        return saveProduct(id, updateProduct);
    }

    public Page<ProductDTO> findAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(product -> mapper.map(product, ProductDTO.class));
    }

    public ProductDTO findById(String id) {
        var product = findProduct(id);
        return mapper.map(product, ProductDTO.class);
    }

    public void deleteByAttribute(String id, String attribute, String value) {
        var product = findProduct(id);
        product.deleteAttributes(attribute, value);
        productRepository.save(product);
    }
    public void delete(String id) {
        productRepository.deleteById(findProduct(id).getId());
    }

    private Product findProduct(String id) {
        return productRepository.findById(id).orElseThrow(() -> new NotFoundException(Constants.NOT_FOUND));
    }

    private ProductDTO saveProduct(String id, Product product) {
        product.setId(id);
        var productUpdated = productRepository.save(product);
        return mapper.map(productUpdated, ProductDTO.class);
    }
}
