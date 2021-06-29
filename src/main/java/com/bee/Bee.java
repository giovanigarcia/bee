package com.bee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Bee {

	public static final int STAR_AND_PLUS_GENERATION_LIMIT = 100;

	private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("\\(\\?'(\\w+)'");

	private static String getAnonymousPattern(String namedPattern) {
		return NAMED_GROUP_PATTERN.matcher(namedPattern).replaceAll("(");
	}

	private final Node treeRoot;

	public Bee(String pattern) throws Exception {
		String anonymousPattern = getAnonymousPattern(pattern);
		Pattern p = Pattern.compile(anonymousPattern);
		Field rootField = Node.getFieldFromClass("root", Pattern.class);
		Object root = rootField.get(p);
		Node n = Node.getNode(Node.NODE_MAPPING.get(root.getClass().getName()), root);

		this.treeRoot = n;
	}
	public String generate() throws Exception {
		return this.treeRoot.generate();
	}

	public static List<String> generateSample(String pattern, int count) throws Exception {
		Bee b = new Bee(pattern);
		List<String> samples = new ArrayList<String>(count);

		for (int i = 0; i < count; i++) {
			samples.add(b.generate());
		}

		return samples;
	}


	public static void main(String[] params) throws Exception {
		if (params.length != 1) {
			System.err.println();
			System.err.println("Usage: java " + Bee.class.getName() + " <regex file>");
			System.err.println("<regex file> should contain 1 regular expression per line");
			System.err.println();
			return;
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(params[0])));
			String line;

			while ((line = br.readLine()) != null) {
				String anonymousPattern = getAnonymousPattern(line);
				Bee bee = new Bee(anonymousPattern);
				System.out.println(bee.generate());
			}
			System.out.flush();
		} finally {
			br.close();
		}
	}

}
