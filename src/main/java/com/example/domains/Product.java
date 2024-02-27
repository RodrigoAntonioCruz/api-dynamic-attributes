package com.example.domains;

import com.example.domains.dto.Attribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product implements Serializable {

    @Serial
    private static final long serialVersionUID = -8271925344794286698L;

    @Id
    private String id;

    @Indexed(unique = true)
    private String description;

    private List<Attribute> attributes;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

}
