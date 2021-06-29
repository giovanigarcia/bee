package com.bee;

import java.lang.reflect.Field;

public class Branch extends Node {

	Branch(Object node) {
		super(node);
	}

	String generate() throws Exception {
		Node chosenAtom = this.pickRandomAtom();
		return (chosenAtom == null ? "" : chosenAtom.generate()) + this.next().generate();
	}
	private Node pickRandomAtom() {
		try {
			Field sizeField = getFieldFromClass("size", this.originalNode.getClass());
			int size = (Integer) sizeField.get(this.originalNode);
			Field atomsField = getFieldFromClass("atoms", this.originalNode.getClass());
			Object[] atoms = (Object[]) atomsField.get(this.originalNode);

			Object chosenAtom = atoms[RNG.nextInt(size)];

			if (chosenAtom == null) {
				return null;
			} else {
				return getNode(NODE_MAPPING.get(chosenAtom.getClass().getName()), chosenAtom);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
