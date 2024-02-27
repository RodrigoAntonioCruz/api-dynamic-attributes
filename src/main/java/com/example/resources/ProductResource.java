package com.example.resources;


import com.example.domains.dto.ProductDTO;
import com.example.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.example.utils.Constants.EMPTY;


@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "Products")
@RequestMapping("/products")
public class ProductResource {

    private final ProductService productService;

    @PostMapping
    @Operation(description = "Cria um novo produto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "409", description = "Duplicidade nos dados informados"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .buildAndExpand(productDTO.getId()).toUri())
                .body(productService.create(productDTO));
    }

    @PatchMapping("/{id}")
    @Operation(description = "Atualiza parcialmente um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "409", description = "Duplicidade nos dados informados"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> patch(@PathVariable String id,
                                            @RequestParam(value = "index", defaultValue = EMPTY) String index, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok().body(productService.patch(id, index, productDTO));
    }

    @PutMapping("/{id}")
    @Operation(description = "Atualiza um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "409", description = "Duplicidade nos dados informados"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> update(@PathVariable String id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok().body(productService.update(id, productDTO));
    }

    @GetMapping("/search")
    @Operation(description = "Busca paginada de produtos por filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados."),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<Page<ProductDTO>> findByKeyword(@RequestParam(value = "keyword") String keyword,
                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "linesPerPage", defaultValue = "100") Integer linesPerPage,
                                                          @RequestParam(value = "direction", defaultValue = "ASC") String direction,
                                                          @RequestParam(value = "orderBy", defaultValue = "id") String orderBy) {
        Page<ProductDTO> products = productService.findByKeyword(keyword, PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy));
        return ResponseEntity.ok().body(products);
    }

    @GetMapping("/{id}")
    @Operation(description = "Busca um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitação realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados"),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> findById(@Valid @PathVariable String id) {
        return ResponseEntity.ok().body(productService.findById(id));
    }

    @DeleteMapping("/attribute/{id}")
    @Operation(description = "Remove um atributo existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitação sem conteúdo realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados."),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> deleteByAttribute(@Valid @PathVariable String id,
                                                        @RequestParam(value = "attribute") String attribute,
                                                        @RequestParam(value = "value", defaultValue = EMPTY) String value) {
        productService.deleteByAttribute(id, attribute, value);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Remove um produto existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Solicitação sem conteúdo realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Inconsistência nos dados informados."),
            @ApiResponse(responseCode = "401", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "500", description = "Sistema indisponível no momento")})
    public ResponseEntity<ProductDTO> delete(@Valid @PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}