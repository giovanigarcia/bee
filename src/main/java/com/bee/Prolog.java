package com.bee;

import java.lang.reflect.Field;

public class Prolog extends Node {

	Prolog(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field loopField = getFieldFromClass("loop", originalNode.getClass());
		Object loop = loopField.get(this.originalNode);
		
		Node node = (Node) getNode(NODE_MAPPING.get(loop.getClass().getName()), loop);
		
		return node.generate() + this.next().generate();
	}

}
