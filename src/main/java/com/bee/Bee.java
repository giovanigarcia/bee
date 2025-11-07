package com.bee;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Model.ArgGroupSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;
import picocli.CommandLine.UnmatchedArgumentException;

public class Bee {

	public static final String[] HELP_CLI = { "-h", "--help" };
	public static final String[] VERSION_CLI = { "-v", "--version" };

	public static final String[] REGEX_CLI = { "-r", "--regex" };
	public static final String[] INPUT_REGEX_FILE_CLI = { "-i", "--input" };
	public static final String[] PATTERN_COUNT_CLI = { "-c", "--count" };


	public static final OptionSpec HELP_OPTION =
			OptionSpec.builder(HELP_CLI)
						.usageHelp(true)
						.description("Show this help message and exit")
						.build();
	public static final OptionSpec VERSION_OPTION =
			OptionSpec.builder(VERSION_CLI)
						.versionHelp(true)
						.description("Print version information and exit")
						.build();

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
	public static final ArgGroupSpec INPUT_EXCLUSIVE_GROUP =
			ArgGroupSpec.builder()
						.addArg(REGEX_OPTION)
						.addArg(INPUT_REGEX_FILE_OPTION)
						.build();

	public static final CommandSpec COMMAND_SPEC = CommandSpec.create()
			.name(Bee.class.getPackage().getImplementationTitle())
			.versionProvider(new ManifestVersionProvider())
			.addOption(HELP_OPTION)
			.addOption(VERSION_OPTION)
			.addArgGroup(INPUT_EXCLUSIVE_GROUP)
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
		final List<String> samples = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			samples.add(b.generate());
		}

		return samples;
	}


	public static void main(final String[] args) throws Exception {
		final CommandLine parser = new CommandLine(COMMAND_SPEC);
		ParseResult cmd = null;

		try {
			cmd = parser.parseArgs(args);
		} catch (ParameterException e) {
			parser.getErr().println(e.getMessage());

			UnmatchedArgumentException.printSuggestions(e, parser.getErr());

			return;
		}


		if (cmd.isUsageHelpRequested()) {
			parser.usage(parser.getErr());
			return;
		}
		if (cmd.isVersionHelpRequested()) {
			parser.printVersionHelp(parser.getErr());
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
		try (BufferedReader br = new BufferedReader(new FileReader(new File(input)))) {
			String line;

			while ((line = br.readLine()) != null) {
				final Bee bee = new Bee(line);
				for (int i=0; i<count; i++) {
					System.out.println(bee.generate());
				}
			}
			System.out.flush();
		}
	}

}


class ManifestVersionProvider implements IVersionProvider {

	@Override
	public String[] getVersion() throws Exception {
		String implementationTitle = Bee.class.getPackage().getImplementationTitle();
		if (implementationTitle == null) {
			implementationTitle = "<unnamed-binary>";
		}

		String implementationVersion = Bee.class.getPackage().getImplementationVersion();
		if (implementationVersion == null) {
			implementationVersion = "<unknown-version>";
		}

		return new String[] { implementationTitle + "-" + implementationVersion };
	}

}