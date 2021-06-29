package com.bee;

import java.util.ArrayList;
import java.util.List;

public class Dot extends Node {

	Dot(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		List<Character> possibleValues = new ArrayList<Character>(256);
		for (int i=0; i < 256; ++i) {
			possibleValues.add((char) i);
		}

		return "" + possibleValues.get(RNG.nextInt(possibleValues.size()));
	}

}
