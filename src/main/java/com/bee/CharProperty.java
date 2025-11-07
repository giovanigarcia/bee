package com.bee;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for java.util.regex.Pattern$CharProperty (JDK 9+)
 * Used for patterns like . (dot)
 */
public class CharProperty extends Node {

    CharProperty(Object node) {
        super(node);
    }

    @Override
    String generate() throws Exception {
        Field predicateField = getFieldFromClass("predicate", originalNode.getClass());
        Object predicate = predicateField.get(this.originalNode);

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
        List<Character> matches = new ArrayList<Character>();

        // Test printable ASCII first (most common for . is printable chars)
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
            throw new Exception("No matching characters found for CharProperty");
        }

        char selectedChar = matches.get(RNG.nextInt(matches.size()));
        return selectedChar + this.next().generate();
    }
}
