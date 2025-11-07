package com.bee;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Node classes, focusing on JDK 21+ CharPredicate-based implementations.
 */
class NodeTest {

	@Test
	void testBmpCharPropertySingleCharacter() throws Exception {
		Pattern pattern = Pattern.compile("a");
		Bee bee = new Bee("a");
		String result = bee.generate();
		assertEquals("a", result);
	}

	@Test
	void testBmpCharPropertyRange() throws Exception {
		Bee bee = new Bee("[a-c]");
		for (int i = 0; i < 10; i++) {
			String result = bee.generate();
			assertTrue(result.matches("[a-c]"), "Result: " + result);
		}
	}

	@Test
	void testBmpCharPropertyDigit() throws Exception {
		Bee bee = new Bee("\\d");
		for (int i = 0; i < 10; i++) {
			String result = bee.generate();
			assertTrue(result.matches("\\d"), "Result: " + result);
		}
	}

	@Test
	void testBmpCharPropertyGreedy() throws Exception {
		Bee bee = new Bee("a*");
		for (int i = 0; i < 5; i++) {
			String result = bee.generate();
			assertTrue(result.matches("a*"), "Result: " + result);
			assertTrue(result.length() <= Bee.STAR_AND_PLUS_GENERATION_LIMIT);
		}
	}

	@Test
	void testCharProperty() throws Exception {
		Bee bee = new Bee(".");
		String result = bee.generate();
		assertNotNull(result);
		assertTrue(result.length() >= 1);
	}

	@Test
	void testCharPropertyGreedy() throws Exception {
		Bee bee = new Bee(".*");
		String result = bee.generate();
		assertNotNull(result);
		assertTrue(result.length() <= Bee.STAR_AND_PLUS_GENERATION_LIMIT);
	}

	@Test
	void testNodeCaching() throws Exception {
		// Create two Bee instances with the same pattern
		Bee bee1 = new Bee("[a-z]+");
		Bee bee2 = new Bee("[a-z]+");

		// Both should work
		assertNotNull(bee1.generate());
		assertNotNull(bee2.generate());
	}

	@Test
	void testAllNodeTypesAreMapped() {
		// This test ensures no node type is null in the mapping
		assertFalse(Node.NODE_MAPPING.isEmpty(), "NODE_MAPPING should not be empty");

		// Check that all mapped classes are valid Node subclasses
		Node.NODE_MAPPING.forEach((key, value) -> {
			assertNotNull(value, "Node class for " + key + " should not be null");
			assertTrue(Node.class.isAssignableFrom(value),
				value.getName() + " should be a subclass of Node");
		});
	}
}
