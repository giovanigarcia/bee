package com.bee;

import java.lang.reflect.Field;

public class SquareBracketRange extends Node {

	SquareBracketRange(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field lhsField = getFieldFromClass("val$lhs", originalNode.getClass());
		Object lhs = lhsField.get(this.originalNode);
		Node lhsNode = (Node) getNode(NODE_MAPPING.get(lhs.getClass().getName()), lhs);

		Field rhsField = getFieldFromClass("val$rhs", originalNode.getClass());
		Object rhs = rhsField.get(this.originalNode);
		Node rhsNode = (Node) getNode(NODE_MAPPING.get(rhs.getClass().getName()), rhs);

		return lhsNode.generate() + rhsNode.generate() + this.next().generate();
	}

}
