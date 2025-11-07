package com.bee;

public class StartS extends Node {

	StartS(Object node) {
		super(node);
	}

	@Override
	String generate() throws Exception {
		return this.next().generate();
	}

}
