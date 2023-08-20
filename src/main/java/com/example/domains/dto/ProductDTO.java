package com.example.domains.dto;


import com.example.utils.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4011786761694912983L;

    @Schema(defaultValue = "Identificador de um produto", example = "648f0e313214234e611ecbdb")
    private String id;

    @NotBlank(message = Constants.DESCRIPTION_NOT_NULL)
    @Length(min=3, max=1000, message = Constants.DESCRIPTION_MAX_LENGTH)
    @Schema(defaultValue = "Descrição de um produto", example = "Hidratante Mustela Stelatopia Pele Ressecada e Atópica 500ml")
    private String description;

    @Schema(defaultValue = "Atributos dinâmicos de um produto", example = "{\"year\": 2023, \"value\": 100.0, \"ids\": [1, 2, 3], \"set\": [\"A\", \"B\", \"C\"], \"description\": \"Product description\", \"categories\": [{\"id\": 1, \"name\": \"Electronics\"}, {\"id\": 2, \"name\": \"Computers\"}]}")
    private Map<String, Object> attributes;

    @Schema(defaultValue = "Data de criação", example = "05/09/2022")
    private LocalDateTime createdAt;

    @Schema(defaultValue = "Data de atualização", example = "01/02/2023")
    private LocalDateTime updatedAt;
}