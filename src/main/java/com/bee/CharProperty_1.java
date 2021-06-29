package com.bee;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CharProperty_1 extends Node {

	CharProperty_1(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		Field _this$0Field = getFieldFromClass("this$0", this.originalNode.getClass());
		Object _this$0 = _this$0Field.get(this.originalNode);

		Field ctypeField = getFieldFromClass("ctype", _this$0.getClass());
		Number ctype = (Number) ctypeField.get(_this$0);

		List<Character> possibleValues = new ArrayList<Character>(256);
		for (int i=0; i < 256; ++i) {
			possibleValues.add((char) i);
		}

		Character[] toRemove = new Character[] {};
		switch (ctype.intValue()) {
			case 1024:
				// \D
				toRemove = new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };
				break;
			case 2048:
				// \S - missing \x0B
				toRemove = new Character[] { '\t', '\n', '\f', '\r', };
				break;
			case 67328:
				// \W
				toRemove = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
											 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
											 'u', 'v', 'w', 'x', 'y', 'z',
											 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
											 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
											 'U', 'V', 'W', 'X', 'Y', 'Z',
											 '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
											 '_', };
				break;
		}
		possibleValues.removeAll(Arrays.asList(toRemove));

		return "" + possibleValues.get(RNG.nextInt(possibleValues.size()));
	}

}
