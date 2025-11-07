package com.bee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Bee regex text generator.
 * Tests various regex patterns to ensure all Pattern node types are mapped correctly.
 */
class BeeIntegrationTest {

	@Test
	void testSimplePattern() throws Exception {
		Bee bee = new Bee("hello");
		String result = bee.generate();
		assertEquals("hello", result);
	}

	@Test
	void testCharacterClass() throws Exception {
		Bee bee = new Bee("[a-z]");
		String result = bee.generate();
		assertNotNull(result);
		assertEquals(1, result.length());
		assertTrue(result.charAt(0) >= 'a' && result.charAt(0) <= 'z');
	}

	@Test
	void testDigits() throws Exception {
		Bee bee = new Bee("\\d{2}");
		String result = bee.generate();
		assertNotNull(result);
		assertEquals(2, result.length());
		assertTrue(result.matches("\\d{2}"));
	}

	@Test
	void testAlternation() throws Exception {
		Bee bee = new Bee("foo|bar");
		String result = bee.generate();
		assertTrue(result.equals("foo") || result.equals("bar"));
	}

	@Test
	void testQuantifiers() throws Exception {
		Bee bee = new Bee("a{2,4}");
		String result = bee.generate();
		assertTrue(result.length() >= 2 && result.length() <= 4);
		assertTrue(result.matches("a{2,4}"));
	}

	/**
	 * Comprehensive test for various regex patterns.
	 * This catches missing node mappings that only appear with specific patterns.
	 */
	@ParameterizedTest
	@ValueSource(strings = {
		// Basic patterns
		"a", "abc", "[a-z]", "[A-Z]", "[0-9]",

		// Quantifiers
		"a?", "a*", "a+", "a{2}", "a{2,5}",

		// Character classes
		"\\d", "\\D", "\\w", "\\W", "\\s", "\\S",

		// Dot
		".",

		// Anchors
		"^start", "end$",

		// Groups
		"(abc)", "(foo|bar)",

		// Combined patterns
		"[a-z]{3,5}", "\\d{2,4}", "[A-Z][a-z]+",

		// Character class quantifiers (these exposed the StartS bug)
		"\\w{1}", "\\W{1}", "\\d{1,3}", "\\s+",

		// Complex patterns
		"[a-zA-Z0-9]+", "\\d{3}-\\d{4}", "(foo|bar|baz){1,2}"
	})
	void testVariousPatterns(String pattern) throws Exception {
		Bee bee = new Bee(pattern);
		String result = bee.generate();

		assertNotNull(result, "Generated text should not be null for pattern: " + pattern);

		// Verify the generated text matches the original pattern
		Pattern compiledPattern = Pattern.compile(pattern);
		assertTrue(compiledPattern.matcher(result).find(),
			String.format("Generated text '%s' should match pattern '%s'", result, pattern));
	}

	@Test
	void testGenerateSample() throws Exception {
		List<String> samples = Bee.generateSample("[a-z]{3}", 10);

		assertEquals(10, samples.size());
		for (String sample : samples) {
			assertEquals(3, sample.length());
			assertTrue(sample.matches("[a-z]{3}"));
		}
	}

	@Test
	void testNamedGroupRemoval() throws Exception {
		// Named groups should be converted to anonymous groups
		Bee bee = new Bee("(?'name'abc)");
		String result = bee.generate();
		assertEquals("abc", result);
	}

	@Test
	void testStarAndPlusLimit() throws Exception {
		// Star and plus should respect the generation limit
		Bee bee = new Bee("a*");
		String result = bee.generate();
		assertTrue(result.length() <= Bee.STAR_AND_PLUS_GENERATION_LIMIT,
			"Generated text length should not exceed STAR_AND_PLUS_GENERATION_LIMIT");
	}
}
