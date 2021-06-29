package com.bee;

import java.lang.reflect.Field;

public class Single extends Node {

	Single(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field cField = getFieldFromClass("c", originalNode.getClass());
		Number c = (Number) cField.get(this.originalNode);

		return ((char) c.intValue()) + this.next().generate();
	}

}
