package com.bee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.UnmatchedArgumentException;

public class Bee {

	public static final String[] REGEX_CLI = { "-r", "--regex" };
	public static final String[] INPUT_REGEX_FILE_CLI = { "-i", "--input" };
	public static final String[] PATTERN_COUNT_CLI = { "-c", "--count" };

	public static final OptionSpec REGEX_OPTION =
			OptionSpec.builder(REGEX_CLI)
						.description("Regular expression used as pattern for text generation")
						.paramLabel("<regex>")
						.arity("1")
						.build();
	public static final OptionSpec INPUT_REGEX_FILE_OPTION =
			OptionSpec.builder(INPUT_REGEX_FILE_CLI)
						.description("Input file where regular expressions will be found. One regular expression per line")
						.paramLabel("<input file>")
						.arity("1")
						.build();
	public static final OptionSpec PATTERN_COUNT_OPTION =
			OptionSpec.builder(PATTERN_COUNT_CLI)
						.description("The number of patterns to generate for each regular expression provided (defaults to 1)")
						.paramLabel("<count>")
						.arity("1")
						.type(Long.class)
						.build();

	public static final CommandSpec COMMAND_SPEC = CommandSpec.create()
			.addOption(REGEX_OPTION)
			.addOption(INPUT_REGEX_FILE_OPTION)
			.addOption(PATTERN_COUNT_OPTION);


	public static final int STAR_AND_PLUS_GENERATION_LIMIT = 100;

	private static final Pattern NAMED_GROUP_PATTERN = Pattern.compile("\\(\\?'(\\w+)'");

	private static String getAnonymousPattern(final String namedPattern) {
		return NAMED_GROUP_PATTERN.matcher(namedPattern).replaceAll("(");
	}

	private final Node treeRoot;

	public Bee(final String pattern) throws Exception {
		final String anonymousPattern = getAnonymousPattern(pattern);
		final Pattern p = Pattern.compile(anonymousPattern);
		final Field rootField = Node.getFieldFromClass("root", Pattern.class);
		final Object root = rootField.get(p);
		final Node n = Node.getNode(Node.NODE_MAPPING.get(root.getClass().getName()), root);

		this.treeRoot = n;
	}
	public String generate() throws Exception {
		return this.treeRoot.generate();
	}

	public static List<String> generateSample(final String pattern, final int count) throws Exception {
		final Bee b = new Bee(pattern);
		final List<String> samples = new ArrayList<String>(count);

		for (int i = 0; i < count; i++) {
			samples.add(b.generate());
		}

		return samples;
	}


	public static void main(final String[] args) throws Exception {
		final CommandLine parser = new CommandLine(COMMAND_SPEC);
		final ParseResult cmd;

		try {
			cmd = parser.parseArgs(args);
		} catch (ParameterException e) {
			parser.getErr().println(e.getMessage());

			UnmatchedArgumentException.printSuggestions(e, parser.getErr());

			return;
		}

		Long count = PATTERN_COUNT_OPTION.getValue();
		if (count == null) {
			count = 1L;
		}

		final String inputFile = INPUT_REGEX_FILE_OPTION.getValue();
		if (inputFile != null) {
			handleFile(inputFile, count);
			return;
		}

		final String regex = REGEX_OPTION.getValue();
		if (regex != null) {
			final Bee bee = new Bee(regex);
			for (int i=0; i<count; i++) {
				System.out.println(bee.generate());
			}
			return;
		}
	}

	private static final void handleFile(final String input, final long count) throws Exception {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(input)));
			String line;

			while ((line = br.readLine()) != null) {
				final Bee bee = new Bee(line);
				for (int i=0; i<count; i++) {
					System.out.println(bee.generate());
				}
			}
			System.out.flush();
		} finally {
			br.close();
		}
	}

}
