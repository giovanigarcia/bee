package com.bee;

import java.lang.reflect.Field;

public class Loop extends Node {

	private int maxLoopCount = -1;
	private int currentLoopCount = 0;

	Loop(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field cminField = getFieldFromClass("cmin", originalNode.getClass());
		Number cmin = (Number) cminField.get(this.originalNode);
		Field cmaxField = getFieldFromClass("cmax", originalNode.getClass());
		Number cmax = (Number) cmaxField.get(this.originalNode);

		if (this.maxLoopCount == -1) {
			this.maxLoopCount = randomInt(cmin, cmax);
		}

		if (this.currentLoopCount < this.maxLoopCount) {
			Field bodyField = getFieldFromClass("body", originalNode.getClass());
			Object body = bodyField.get(this.originalNode);
			Node node = (Node) getNode(NODE_MAPPING.get(body.getClass().getName()), body);
			this.currentLoopCount++;
			return node.generate();
		} else {
			resetState();
			return this.next().generate();
		}
	}

	private void resetState() {
		this.maxLoopCount = -1;
		this.currentLoopCount = 0;
	}

}
