package com.bee;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BitClass extends Node {

	BitClass(Object node) {
		super(node);
	}

	String generate() throws Exception {
		Field bitsField = getFieldFromClass("bits", originalNode.getClass());
		boolean[] bits = (boolean[]) bitsField.get(this.originalNode);

		List<Character> possibleValues = new ArrayList<Character>(256);
		for (int i=0; i < bits.length; ++i) {
			if (bits[i]) {
				possibleValues.add((char) i);
			}
		}

		return possibleValues.get(RNG.nextInt(possibleValues.size())) + this.next().generate();
	}

}