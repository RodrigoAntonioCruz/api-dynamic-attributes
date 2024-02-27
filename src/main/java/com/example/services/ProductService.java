package com.example.services;


import com.example.domains.Product;
import com.example.domains.dto.ProductDTO;
import com.example.exceptions.NotFoundException;
import com.example.repositories.ProductRepository;
import com.example.repositories.ProductRepositoryCustom;
import com.example.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static com.example.mappers.ProductMapper.INSTANCE;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    private final ProductRepositoryCustom repositoryCustom;

    public ProductDTO create(ProductDTO productDTO) {
        var product = INSTANCE.toProductEntity(productDTO);
        return saveProduct(null, product);
    }

    public ProductDTO patch(String id, String index, ProductDTO productDTO) {
        Product existingProduct = findProductById(id);

        if (!StringUtils.isEmpty(productDTO.getDescription())) {
            existingProduct.setDescription(productDTO.getDescription());
        }

        if (productDTO.getAttributes() != null) {
            var attributes = INSTANCE.patchAttributes(productDTO.getAttributes(), existingProduct.getAttributes(), index);
            existingProduct.setAttributes(attributes);
        }

        return saveProduct(id, existingProduct);
    }

    public ProductDTO update(String id, ProductDTO productDTO) {
        var existingProduct = findProductById(id);
        var existingAttributes = existingProduct.getAttributes();

        var updatedAttributes = INSTANCE.updateAttributes(productDTO.getAttributes(), existingAttributes);
        existingProduct.setDescription(productDTO.getDescription());

        existingProduct.setAttributes(updatedAttributes);
        return saveProduct(id, existingProduct);
    }

    public Page<ProductDTO> findByKeyword(String keyword, Pageable pageable) {
        return repositoryCustom.findByKeyword(keyword, pageable).map(INSTANCE::toProductDTO);
    }
    public ProductDTO findById(String id) {
        var product = findProductById(id);
        return INSTANCE.toProductDTO(product);
    }

    public void deleteByAttribute(String id, String attribute, String value) {
        var product = findProductById(id);
        INSTANCE.deleteAttributes(attribute, value, product.getAttributes());
        repository.save(product);
    }

    public void delete(String id) {
        findProductById(id);
        repository.deleteById(findProductById(id).getId());
    }

    private Product findProductById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(Constants.NOT_FOUND));
    }

    private ProductDTO saveProduct(String id, Product product) {
        product.setId(id);
        var productUpdated = repository.save(product);
        return INSTANCE.toProductDTO(productUpdated);
    }
}
