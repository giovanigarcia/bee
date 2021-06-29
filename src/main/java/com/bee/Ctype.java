package com.bee;

import java.lang.reflect.Field;

public class Ctype extends Node {

	Ctype(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field ctypeField = getFieldFromClass("ctype", originalNode.getClass());
		Number ctype = (Number) ctypeField.get(this.originalNode);

		Character[] options = new Character[] { ' ', };
		switch (ctype.intValue()) {
			case 1024:
				// \D
				options = new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };
				break;
			case 2048:
				// \S - missing \x0B
				options = new Character[] { '\t', '\n', '\f', '\r', };
				break;
			case 67328:
				// \W
				options = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
											'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
											'u', 'v', 'w', 'x', 'y', 'z',
											'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
											'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
											'U', 'V', 'W', 'X', 'Y', 'Z',
											'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
											'_', };
				break;
		}
		return "" + options[RNG.nextInt(options.length)];
	}

}
