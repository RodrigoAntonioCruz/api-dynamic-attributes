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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


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

    public Map<String, Object> updateAttributes(Map<String, Object> newAttributes) {
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        if (newAttributes != null) {
            for (Map.Entry<String, Object> entry : newAttributes.entrySet()) {
                String attributeName = entry.getKey();
                Object newValue = entry.getValue();

                if (newValue instanceof Collection) {
                    Collection<Object> currentValues = new ArrayList<>();
                    if (updatedAttributes.containsKey(attributeName)) {
                        currentValues.addAll((Collection<Object>) updatedAttributes.get(attributeName));
                    }

                    for (Object value : (Collection<Object>) newValue) {
                        if (!currentValues.contains(value)) {
                            currentValues.add(value);
                        }
                    }

                    updatedAttributes.put(attributeName, currentValues);
                } else {
                    updatedAttributes.put(attributeName, newValue);
                }
            }
        }

        return updatedAttributes;
    }


    public Map<String, Object> deleteAttributes(String attribute, String value) {
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        Object attributeValue = updatedAttributes.get(attribute);
        if (attributeValue != null) {
            if (attributeValue instanceof Collection) {
                removeValueFromList((Collection<?>) attributeValue, value);
            } else if (attributeValue instanceof Map) {
                removeValueFromMap((Map<?, ?>) attributeValue, value);
            } else if (valueMatches(attributeValue, value)) {
                updatedAttributes.remove(attribute);
            }
        }

        attributes = updatedAttributes;
        return updatedAttributes;
    }

    private void removeValueFromList(Collection<?> list, String value) {
        list.removeIf(item -> valueMatches(item, value) || shouldRemove(item, value));
    }

    private void removeValueFromMap(Map<?, ?> map, String value) {
        map.entrySet().removeIf(entry -> valueMatches(entry.getValue(), value) || shouldRemove(entry.getValue(), value));
    }

    private boolean shouldRemove(Object item, Object value) {
        if (item instanceof Map<?, ?> itemMap) {
            return itemMap.containsValue(value);
        } else if (item instanceof Set<?> itemSet) {
            return itemSet.contains(value);
        }
        return false;
    }


    private boolean valueMatches(Object item, String value) {
        if (item instanceof Number numberValue) {
            if (isInteger(value) && numberValue instanceof Integer) {
                return numberValue.intValue() == Integer.parseInt(value);
            } else if (isLong(value) && numberValue instanceof Long) {
                return numberValue.longValue() == Long.parseLong(value);
            } else if (isFloat(value) && numberValue instanceof Float) {
                return numberValue.floatValue() == Float.parseFloat(value);
            } else if (isDouble(value) && numberValue instanceof Double) {
                return numberValue.doubleValue() == Double.parseDouble(value);
            } else {
                try {
                    BigDecimal itemValue = new BigDecimal(numberValue.toString());
                    BigDecimal targetValue = new BigDecimal(value);
                    return itemValue.compareTo(targetValue) == 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return Objects.equals(item, value);
    }


    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
