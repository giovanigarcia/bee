package com.bee;

import java.lang.reflect.Field;

public class Range extends Node {

	Range(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field lowerField = getFieldFromClass("val$lower", originalNode.getClass());
		Number lower = (Number) lowerField.get(this.originalNode);
		Field upperField = getFieldFromClass("val$upper", originalNode.getClass());
		Number upper = (Number) upperField.get(this.originalNode);

		return (char) randomInt(lower, upper) + this.next().generate();
	}

}
