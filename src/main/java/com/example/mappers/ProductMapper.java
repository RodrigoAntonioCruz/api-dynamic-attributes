package com.example.mappers;

import com.example.domains.Product;
import com.example.domains.dto.Attribute;
import com.example.domains.dto.ProductDTO;
import io.fabric8.kubernetes.client.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.utils.Constants.DATE_FORMAT;
import static com.example.utils.Constants.INVALID_DATE_FORMAT;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    ProductDTO toProductDTO(Product product);

    Product toProductEntity(ProductDTO productDto);

    default List<Attribute> updateAttributes(List<Attribute> newAttributes, List<Attribute> attributes) {
        if (newAttributes != null) {
            newAttributes.forEach(newAttribute -> {
                String attributeName = newAttribute.getKey();
                Object newValue = newAttribute.getValue();

                if (newValue == null) {
                    attributes.removeIf(attribute -> attribute.getKey().equals(attributeName));
                } else if (newValue instanceof Collection) {
                    updateCollectionAttribute(attributes, attributeName, (Collection<?>) newValue);
                } else {
                    Attribute existingAttribute = attributes.stream()
                            .filter(attribute -> attribute.getKey().equals(attributeName))
                            .findFirst()
                            .orElse(null);

                    if (existingAttribute != null) {
                        existingAttribute.setValue(newValue);
                    } else {
                        attributes.add(new Attribute(attributeName, newValue));
                    }
                }
            });
        }
        attributes.removeIf(attribute -> Objects.requireNonNull(newAttributes).stream()
                .noneMatch(newAttribute -> newAttribute.getKey().equals(attribute.getKey())));

        return attributes;
    }

    default void updateCollectionAttribute(List<Attribute> updatedAttributes, String attributeName, Collection<?> newValue) {
        Attribute attribute = updatedAttributes.stream()
                .filter(attr -> attr.getKey().equals(attributeName))
                .findFirst()
                .orElse(null);

        if (attribute != null) {
            List<Object> currentValues = new ArrayList<>(newValue);
            currentValues = currentValues.stream().distinct().collect(Collectors.toList());
            attribute.setValue(currentValues);
        } else {
            List<Object> currentValues = new ArrayList<>(newValue);
            currentValues = currentValues.stream().distinct().collect(Collectors.toList());
            updatedAttributes.add(new Attribute(attributeName, currentValues));
        }
    }

    default List<Attribute> patchAttributes(List<Attribute> partialAttributes, List<Attribute> attributes, String index) {
        if (!Utils.isNullOrEmpty(index)) {
            partialAttributes.forEach(partialAttribute -> {
                String attributeName = partialAttribute.getKey();
                Object newValue = partialAttribute.getValue();

                Attribute existingAttribute = findAttributeByName(attributes, attributeName);
                if (existingAttribute != null && existingAttribute.getValue() instanceof List<?>) {
                    List<Object> existingList = (List<Object>) existingAttribute.getValue();
                    int idx = Integer.parseInt(index);
                    if (idx >= 0 && idx < existingList.size()) {
                        Object oldValue = existingList.get(idx);
                        if (!newValue.equals(oldValue) && !existingList.contains(newValue)) {
                            existingList.set(idx, newValue);
                            existingList.remove(oldValue);
                        }
                    }
                }
            });
        } else {
            partialAttributes.forEach(partialAttribute -> {
                String attributeName = partialAttribute.getKey();
                Object newValue = partialAttribute.getValue();

                if (newValue == null) {
                    attributes.removeIf(attribute -> attribute.getKey().equals(attributeName));
                } else {
                    Attribute existingAttribute = findAttributeByName(attributes, attributeName);
                    if (existingAttribute == null) {
                        attributes.add(new Attribute(attributeName, newValue));
                    } else {

                        if (existingAttribute.getValue() instanceof Collection && newValue instanceof Collection<?> newValues) {
                            Collection<Object> existingValues = (Collection<Object>) existingAttribute.getValue();

                            newValues.stream()
                                    .filter(val -> !existingValues.contains(val))
                                    .forEach(existingValues::add);

                            existingValues.removeIf(val -> Collections.frequency(existingValues, val) > 1);
                        } else if (!existingAttribute.getValue().equals(newValue)) {
                            existingAttribute.setValue(newValue);
                        }
                    }
                }
            });
        }
        return attributes;
    }

    default Attribute findAttributeByName(List<Attribute> attributes, String attributeName) {
        return attributes.stream()
                .filter(attribute -> attribute.getKey().equals(attributeName))
                .findFirst()
                .orElse(null);
    }

    default void deleteAttributes(String attribute, String value, List<Attribute> attributes) {
        if (attribute.contains(".")) {
            String[] keyParts = attribute.split("\\.");
            if (keyParts.length == 2) {
                String listName = keyParts[0];
                String subKey = keyParts[1];
                removeItemFromListByKeyAndValue(attributes, listName, subKey, value);
            }
        } else {
            if (value == null || value.isEmpty()) {
                attributes.removeIf(attr -> attr.getKey().equals(attribute));
            } else {
                removeAttribute(attributes, attribute, value);
            }
        }
    }

    default void removeItemFromListByKeyAndValue(List<Attribute> updatedAttributes, String listName, String subKey, String value) {
        updatedAttributes.forEach(attr -> {
            if (attr.getKey().equals(listName)) {
                removeItemFromListBySubKeyAndValue(attr.getValue(), subKey, value);
            }
        });
    }

    default void removeItemFromListBySubKeyAndValue(Object attributeValue, String subKey, String value) {
        if (attributeValue instanceof List<?> itemList) {
            itemList.removeIf(item -> itemMatchesSubKeyAndValue(item, subKey, value));
        }
    }

    default boolean itemMatchesSubKeyAndValue(Object item, String subKey, String value) {
        if (item instanceof Attribute attribute) {
            return attribute.getKey().equals(subKey) && valueMatches(attribute.getValue(), value);
        } else if (item instanceof Map<?, ?> map) {
            Object subKeyValue = map.get(subKey);
            return subKeyValue != null && subKeyValue.toString().equals(value);
        }
        return false;
    }

    default void removeAttribute(List<Attribute> updatedAttributes, String attribute, String value) {
        updatedAttributes.forEach(attr -> {
            if (attr.getKey().equals(attribute)) {
                Object attributeValue = attr.getValue();
                if (attributeValue instanceof Collection) {
                    removeValueFromCollection((Collection<?>) attributeValue, value);
                } else {
                    if (valueMatches(attributeValue, value)) {
                        updatedAttributes.remove(attr);
                    }
                }
            }
        });
    }

    default void removeValueFromCollection(Collection<?> collection, String value) {
        collection.removeIf(item -> valueMatches(item, value) || shouldRemove(item, value));
    }

    default boolean shouldRemove(Object item, Object value) {
        return (item instanceof Map && ((Map<?, ?>) item).containsValue(value)) ||
                (item instanceof Set && ((Set<?>) item).contains(value));
    }

    default boolean valueMatches(Object item, String value) {
        if (isNumeric(value)) {
            if (item instanceof Number numberValue) {
                return numberValue.toString().equals(value) || compareNumberValues(numberValue, value);
            }
            return false;
        }
        return Objects.equals(item, value);
    }

    default boolean compareNumberValues(Number numberValue, String value) {
        try {
            BigDecimal itemValue = new BigDecimal(numberValue.toString());
            BigDecimal targetValue = new BigDecimal(value);
            return itemValue.compareTo(targetValue) == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isNumeric(String value) {
        try {
            new BigDecimal(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    default boolean isDate(String s) {
        try {
            parseDate(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    default LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(DATE_FORMAT));
            } catch (DateTimeParseException e2) {
                throw new IllegalArgumentException(INVALID_DATE_FORMAT + dateStr);
            }
        }
    }
}