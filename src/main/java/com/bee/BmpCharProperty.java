package com.bee;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper for java.util.regex.Pattern$BmpCharProperty (JDK 9+)
 * Replaces: BitClass, Single, CharProperty$1, Ctype from JDK 6
 */
public class BmpCharProperty extends Node {

    BmpCharProperty(Object node) {
        super(node);
    }

    @Override
    String generate() throws Exception {
        Field predicateField = getFieldFromClass("predicate", originalNode.getClass());
        Object predicate = predicateField.get(this.originalNode);

        // Find a method that tests if a character matches
        // Look for any method with signature: boolean method(int)
        Method isMethod = null;

        // Try public methods first
        for (Method m : predicate.getClass().getMethods()) {
            if (m.getParameterCount() == 1 && m.getReturnType() == boolean.class) {
                Class<?> paramType = m.getParameterTypes()[0];
                if (paramType == int.class || paramType == Integer.class) {
                    isMethod = m;
                    break;
                }
            }
        }

        // If not found, try declared methods
        if (isMethod == null) {
            for (Method m : predicate.getClass().getDeclaredMethods()) {
                if (m.getParameterCount() == 1 && m.getReturnType() == boolean.class) {
                    Class<?> paramType = m.getParameterTypes()[0];
                    if (paramType == int.class || paramType == Integer.class) {
                        isMethod = m;
                        break;
                    }
                }
            }
        }

        if (isMethod == null) {
            // Debug output
            System.err.println("ERROR: Could not find character test method");
            System.err.println("Predicate class: " + predicate.getClass().getName());
            System.err.println("Public methods:");
            for (Method m : predicate.getClass().getMethods()) {
                System.err.println("  " + m.getName() + "(" + java.util.Arrays.toString(m.getParameterTypes()) + ") -> " + m.getReturnType());
            }
            System.err.println("Declared methods:");
            for (Method m : predicate.getClass().getDeclaredMethods()) {
                System.err.println("  " + m.getName() + "(" + java.util.Arrays.toString(m.getParameterTypes()) + ") -> " + m.getReturnType());
            }
            throw new Exception("Could not find character test method on CharPredicate");
        }

        isMethod.setAccessible(true);

        // Find all matching characters in BMP (Basic Multilingual Plane)
        List<Character> matches = new ArrayList<>();

        // Test printable ASCII first (common case optimization)
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
            throw new Exception("No matching characters found for BmpCharProperty");
        }

        char selectedChar = matches.get(RNG.nextInt(matches.size()));
        return selectedChar + this.next().generate();
    }
}
