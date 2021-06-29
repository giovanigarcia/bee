package com.bee;

import java.lang.reflect.Field;

public class Ques extends Node {

	Ques(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field atomField = getFieldFromClass("atom", originalNode.getClass());
		Object atom = atomField.get(this.originalNode);

		Node node = (Node) getNode(NODE_MAPPING.get(atom.getClass().getName()), atom);

		return node.generate() + this.next().generate();
	}

}
