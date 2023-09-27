package com.example.domains;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Map<String, Object> updateAttributes(Map<String, Object> newAttributes) {
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        if (newAttributes != null) {
            newAttributes.forEach((attributeName, newValue) -> {
                if (newValue instanceof Collection) {
                    updateCollectionAttribute(updatedAttributes, attributeName, (Collection<?>) newValue);
                } else {
                    updatedAttributes.put(attributeName, newValue);
                }
            });
        }

        return updatedAttributes;
    }

    private void updateCollectionAttribute(Map<String, Object> updatedAttributes, String attributeName, Collection<?> newValue) {
        Collection<Object> currentValues = new ArrayList<>(getOrCreateCollectionAttribute(updatedAttributes, attributeName));

        List<?> validValues = newValue.stream().filter(this::isValidValue).distinct().toList();

        currentValues.clear();
        currentValues.addAll(validValues);

        updatedAttributes.put(attributeName, currentValues);
    }

    public Map<String, Object> patchAttributes(Map<String, Object> partialAttributes) {
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        if (partialAttributes != null) {
            partialAttributes.forEach((attributeName, newValue) -> {
                Object currentValue = updatedAttributes.get(attributeName);

                if (currentValue == null) {
                    updatedAttributes.put(attributeName, newValue);
                } else if (currentValue instanceof Collection && newValue instanceof Collection) {
                    patchCollectionAttribute((Collection<Object>) currentValue, (Collection<?>) newValue);
                } else {
                    updatedAttributes.put(attributeName, newValue);
                }
            });
        }

        return updatedAttributes;
    }

    private void patchCollectionAttribute(Collection<Object> existingCollection, Collection<?> newValues) {
        Set<Object> uniqueValues = new HashSet<>(existingCollection);
        newValues.stream()
                .filter(this::isValidValue)
                .forEach(uniqueValues::add);
        existingCollection.clear();
        existingCollection.addAll(uniqueValues);
    }


    private Collection<?> getOrCreateCollectionAttribute(Map<String, Object> attributes, String attributeName) {
        return Collections.singleton((Collection<?>) attributes.computeIfAbsent(attributeName, key -> new ArrayList<>()));
    }

    private boolean isValidValue(Object value) {
        if (value == null) {
            return false;
        }

        if (value instanceof String stringValue) {
            return !stringValue.trim().isEmpty();
        }

        if (value instanceof Map<?, ?> mapValue) {
            return !mapValue.isEmpty() && mapValue.values().stream().anyMatch(this::isValidValue);
        }

        if (value instanceof Collection<?> collectionValue) {
            return !collectionValue.isEmpty() && collectionValue.stream().anyMatch(this::isValidValue);
        }

        return true;
    }

    public void deleteAttributes(String attribute, String value) {
        Map<String, Object> updatedAttributes = new HashMap<>(attributes);

        if (attribute.contains(".")) {
            String[] keyParts = attribute.split("\\.");
            if (keyParts.length == 2) {
                String listName = keyParts[0];
                String subKey = keyParts[1];
                if (subKey.equals("index")) {
                    removeItemFromListByIndex(updatedAttributes, listName, Integer.parseInt(value));
                } else {
                    removeItemFromListByKeyAndValue(updatedAttributes, listName, subKey, value);
                }
            }
        } else {
            removeAttribute(updatedAttributes, attribute, value);
        }

        attributes = updatedAttributes;
    }

    private void removeItemFromListByIndex(Map<String, Object> updatedAttributes, String listName, int index) {
        List<?> itemList = (List<?>) updatedAttributes.get(listName);

        if (itemList != null && index >= 0 && index < itemList.size()) {
            itemList.remove(index);
        }
    }

    private void removeAttribute(Map<String, Object> updatedAttributes, String attribute, String value) {
        Object attributeValue = updatedAttributes.get(attribute);

        if (attributeValue != null) {
            if (attributeValue instanceof Collection) {
                removeValueFromCollection((Collection<?>) attributeValue, value);
            } else if (attributeValue instanceof Map) {
                removeValueFromMap((Map<?, ?>) attributeValue, value);
            } else if (valueMatches(attributeValue, value)) {
                updatedAttributes.remove(attribute);
            }
        } else {
            updatedAttributes.entrySet().removeIf(entry -> entry.getKey().equals(attribute));
        }
    }

    private void removeValueFromCollection(Collection<?> collection, String value) {
        collection.removeIf(item -> valueMatches(item, value) || shouldRemove(item, value));
    }

    private void removeValueFromMap(Map<?, ?> map, String value) {
        map.entrySet().removeIf(entry -> valueMatches(entry.getValue(), value) || shouldRemove(entry.getValue(), value));
    }

    private void removeItemFromListByKeyAndValue(Map<String, Object> updatedAttributes, String listName, String subKey, String value) {
        List<?> itemList = (List<?>) updatedAttributes.get(listName);

        if (itemList != null) {
            itemList.removeIf(item -> Objects.equals(((Map<?, ?>) item).get(subKey), Integer.parseInt(value)));
        }
    }

    private boolean shouldRemove(Object item, Object value) {
        return (item instanceof Map && ((Map<?, ?>) item).containsValue(value)) ||
                (item instanceof Set && ((Set<?>) item).contains(value));
    }

    private boolean valueMatches(Object item, String value) {
        if (isNumeric(value)) {
            if (item instanceof Number numberValue) {
                return numberValue.toString().equals(value) || compareNumberValues(numberValue, value);
            }
            return false;
        }
        return Objects.equals(item, value);
    }

    private boolean compareNumberValues(Number numberValue, String value) {
        try {
            BigDecimal itemValue = new BigDecimal(numberValue.toString());
            BigDecimal targetValue = new BigDecimal(value);
            return itemValue.compareTo(targetValue) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isNumeric(String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}