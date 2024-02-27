package com.example.domains.dto;


import com.example.utils.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4011786761694912983L;

    @Schema(defaultValue = "Identificador de um produto", example = "648f0e313214234e611ecbdb")
    private String id;

    @NotBlank(message = Constants.DESCRIPTION_NOT_NULL)
    @Length(min=3, max=1000, message = Constants.DESCRIPTION_MAX_LENGTH)
    @Schema(defaultValue = "Descrição de um produto", example = "Hidratante Mustela Stelatopia Pele Ressecada e Atópica 500ml")
    private String description;

    @Schema(defaultValue = "Atributos dinâmicos de um produto", example = "[{\"key\": \"year\", \"value\": 2023}, {\"key\": \"value\", \"value\": 100.0}, {\"key\": \"ids\", \"value\": [1, 2, 3]}, {\"key\": \"set\", \"value\": [\"A\", \"B\", \"C\"]}, {\"key\": \"description\", \"value\": \"Product description\"}, {\"key\": \"categories\", \"value\": [{\"id\": 1, \"name\": \"Electronics\"}, {\"id\": 2, \"name\": \"Computers\"}]}]")
    private List<Attribute> attributes;
}