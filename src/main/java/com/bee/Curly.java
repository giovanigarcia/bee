package com.bee;

import java.lang.reflect.Field;

public class Curly extends Node {

	Curly(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field cminField = getFieldFromClass("cmin", originalNode.getClass());
		Number cmin = (Number) cminField.get(this.originalNode);
		Field cmaxField = getFieldFromClass("cmax", originalNode.getClass());
		Number cmax = (Number) cmaxField.get(this.originalNode);

		int n = randomInt(cmin, cmax);

		Field atomField = getFieldFromClass("atom", originalNode.getClass());
		Object atom = atomField.get(this.originalNode);
		Node node = (Node) getNode(NODE_MAPPING.get(atom.getClass().getName()), atom);

		StringBuilder sb = new StringBuilder();
		for (int i=0; i < n; i++) {
			sb.append(node.generate());
		}

		return sb.toString() + this.next().generate();
	}

}
