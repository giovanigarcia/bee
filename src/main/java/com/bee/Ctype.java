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

		Character[] options = switch (ctype.intValue()) {
			case 1024 ->
				// \D
				new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
			case 2048 ->
				// \S - missing \x0B
				new Character[] { '\t', '\n', '\f', '\r' };
			case 67328 ->
				// \W
				new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
								  'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
								  'u', 'v', 'w', 'x', 'y', 'z',
								  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
								  'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
								  'U', 'V', 'W', 'X', 'Y', 'Z',
								  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
								  '_' };
			default -> new Character[] { ' ' };
		};
		return "" + options[RNG.nextInt(options.length)];
	}

}
