package com.bee;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for java.util.regex.Pattern$CharPropertyGreedy (JDK 9+)
 * Handles greedy quantifiers like .* and .+ with character properties
 */
public class CharPropertyGreedy extends Node {

    CharPropertyGreedy(Object node) {
        super(node);
    }

    @Override
    String generate() throws Exception {
        Field predicateField = getFieldFromClass("predicate", originalNode.getClass());
        Object predicate = predicateField.get(this.originalNode);

        Field cminField = getFieldFromClass("cmin", originalNode.getClass());
        int cmin = ((Number) cminField.get(this.originalNode)).intValue();

        // Find the 'is' method
        Method isMethod = null;
        for (Method m : predicate.getClass().getMethods()) {
            if (m.getName().equals("is") && m.getParameterCount() == 1) {
                isMethod = m;
                break;
            }
        }

        if (isMethod == null) {
            throw new Exception("Could not find 'is' method on CharPredicate");
        }

        isMethod.setAccessible(true);

        // Find all matching characters
        List<Character> matches = new ArrayList<>();

        // Test printable ASCII first
        for (int i = 32; i < 127; i++) {
            Boolean result = (Boolean) isMethod.invoke(predicate, i);
            if (result) {
                matches.add((char) i);
            }
        }

        // If no ASCII matches, test full BMP range
        if (matches.isEmpty()) {
            for (int i = 0; i < 65536; i++) {
                Boolean result = (Boolean) isMethod.invoke(predicate, i);
                if (result) {
                    matches.add((char) i);
                }
            }
        }

        if (matches.isEmpty()) {
            throw new Exception("No matching characters found for CharPropertyGreedy");
        }

        // Generate repetitions (cmin to limit)
        int repetitions = randomInt(cmin, Bee.STAR_AND_PLUS_GENERATION_LIMIT);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < repetitions; i++) {
            result.append(matches.get(RNG.nextInt(matches.size())));
        }

        return result.toString() + this.next().generate();
    }
}
