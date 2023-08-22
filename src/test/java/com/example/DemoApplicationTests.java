package com.example;

import com.example.domains.enums.CollectionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

@SpringBootTest
class DemoApplicationTests {

	@Test
	void contextLoads() throws JsonProcessingException {
		String json = "{\"value\": 100.0, \"setField\": [\"A\", \"B\", \"C\"],\"categories\": [{\"id\": 1, \"name\": \"Electronics\"}, {\"id\": 2, \"name\": \"Computers\"}]}";

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.readTree(json);

		detectCollectionTypes(rootNode);
	}


	private void detectCollectionTypes(JsonNode rootNode) {
		Iterator<Map.Entry<String, JsonNode>> fields = rootNode.fields();

		while (fields.hasNext()) {
			Map.Entry<String, JsonNode> entry = fields.next();
			JsonNode fieldValue = entry.getValue();

			if (isSimpleAttribute(fieldValue)) {
				System.out.println(CollectionType.SIMPLE);
			} else if (hasDuplicates(fieldValue)) {
				System.out.println(CollectionType.LIST);
			} else {
				System.out.println(CollectionType.SET);
			}
		}
	}


	private static boolean isSimpleAttribute(JsonNode fieldValue) {
		return fieldValue.isTextual() || fieldValue.isNumber() || fieldValue.isBoolean();
	}

	private static boolean hasDuplicates(JsonNode fieldValue) {
		Set<String> uniqueValues = new HashSet<>();
		boolean hasDuplicates = false;

		for (JsonNode element : fieldValue) {
			String valueAsString = element.asText();
			if (!uniqueValues.add(valueAsString)) {
				hasDuplicates = true;
				break;
			}
		}
		return hasDuplicates;
	}



	@Test
	void teste2(){
		AttributeUpdater attributeUpdater = new AttributeUpdater();

		// Exemplo de uso
		Map<String, Object> attributes = new HashMap<>();
		Set<String> letras = new LinkedHashSet<>(Arrays.asList("A", "B", "C", "d"));
		attributes.put("letras", letras);

		Map<String, Object> newAttributes = new HashMap<>();
		Set<String> newLetras = new LinkedHashSet<>(Arrays.asList("N", "A"));
		newAttributes.put("letras", newLetras);

		Map<String, Object> updatedAttributes = attributeUpdater.updateAttributes(newAttributes, attributes);

		System.out.println(updatedAttributes);
	}



	public class AttributeUpdater {
		public Map<String, Object> updateAttributes(Map<String, Object> newAttributes, Map<String, Object> attributes) {
			Map<String, Object> updatedAttributes = new HashMap<>(attributes);

			if (newAttributes != null) {
				for (Map.Entry<String, Object> entry : newAttributes.entrySet()) {
					String attributeName = entry.getKey();
					Object newValue = entry.getValue();

					if (newValue instanceof Set) {
						Set<Object> currentValues = new LinkedHashSet<>();
						if (updatedAttributes.containsKey(attributeName)) {
							currentValues.addAll((Set<Object>) updatedAttributes.get(attributeName));
						}
						currentValues.addAll((Set<Object>) newValue);
						updatedAttributes.put(attributeName, currentValues);
					} else if (newValue instanceof String) {
						Set<String> uniqueValues = new LinkedHashSet<>();
						if (updatedAttributes.containsKey(attributeName)) {
							uniqueValues.addAll((Set<String>) updatedAttributes.get(attributeName));
						}
						uniqueValues.add((String) newValue);
						updatedAttributes.put(attributeName, uniqueValues);
					}
				}
			}

			return updatedAttributes;
		}}
}
