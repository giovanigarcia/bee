package com.bee;

import java.lang.reflect.Field;

public class BnM extends Node {

	BnM(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field bufferField = getFieldFromClass("buffer", originalNode.getClass());
		int[] buffer = (int[]) bufferField.get(this.originalNode);

		StringBuilder sb = new StringBuilder();
		for (int i : buffer) {
			sb.append((char) i);
		}

		return sb.toString() + this.next().generate();
	}

}
