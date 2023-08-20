package com.example.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
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
    private Map<String, Object> attributes = new HashMap<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Map<String, Object> updateDynamicAttributes(Map<String, Object> newAttributes) {

        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        if (newAttributes != null) {
            for (Map.Entry<String, Object> entry : newAttributes.entrySet()) {
                String attributeName = entry.getKey();
                Object newValue = entry.getValue();

                updatedAttributes.put(attributeName, newValue);
            }
        }
        return updatedAttributes;
    }
}
