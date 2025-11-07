bee
===

Generates text matching Regular Expressions

## Overview

Bee is a Java library and command-line tool that generates random text matching given regular expression patterns. It uses reflection to access the internal structure of Java's `java.util.regex.Pattern` class and traverses the compiled pattern tree to generate valid matching strings.

This tool is particularly useful for:
- Generating test data matching specific patterns
- Creating sample data for validation testing
- Fuzzing applications that accept regex-constrained input
- Prototyping regex patterns with concrete examples

**Key Features:**
- Supports most standard regex features (character classes, quantifiers, alternation, groups, anchors)
- Generates multiple samples from the same pattern
- Programmatic API for use in Java applications

## Requirements

- **Java SE 21 or higher** (tested with Java 21)
- The application uses reflection to access internal `java.util.regex.Pattern` classes
- The `--add-opens java.base/java.util.regex=ALL-UNNAMED` JVM flag is required (configured automatically in Gradle build)

## Building

```bash
./gradlew build
```

The distribution will be created in `build/distributions/`.

## Usage Examples

### Command-Line: Single Pattern

Generate 5 samples matching a pattern:

```bash
bee -r '[a-z]{3,5}' -c 5
```

Example output:
```
abc
defg
xyz
abcde
pqr
```

### Command-Line: Pattern File

Create a file `patterns.txt` with one pattern per line:
```
[a-z]{3,5}
\d{2,4}
hello|world
[A-Z][a-z]+
\d{3}-\d{4}
```

Run:
```bash
bee -i patterns.txt
```

This generates one sample for each pattern by default. Use `-c` to generate multiple samples per pattern:

```bash
bee -i patterns.txt -c 3
```

### Programmatic Usage

Use Bee in your Java application:

```java
import com.bee.Bee;
import java.util.List;

// Generate a single sample
Bee bee = new Bee("[a-z]{3,5}");
String sample = bee.generate();
System.out.println(sample);  // e.g., "abc"

// Generate multiple samples
List<String> samples = Bee.generateSample("\\d{3}-\\d{4}", 10);
samples.forEach(System.out::println);
```

### Command-Line Options

```
Usage: bee [-hv] [-c=<count>] [-r=<regex> | -i=<input file>]
  -c, --count=<count>        The number of patterns to generate for each
                               regular expression provided (defaults to 1)
  -h, --help                 Show this help message and exit
  -i, --input=<input file>   Input file where regular expressions will be
                               found. One regular expression per line
  -r, --regex=<regex>        Regular expression used as pattern for text
                               generation
  -v, --version              Print version information and exit
```

## How It Works

Bee uses reflection to access the internal node structure of Java's compiled `Pattern` objects. The Pattern compiler creates a tree of node objects representing different regex components (character classes, quantifiers, groups, etc.). Bee traverses this tree and generates valid matching text by:

1. Extracting the internal `Pattern.root` node via reflection
2. Creating wrapper classes for each node type (e.g., `BmpCharProperty`, `Curly`, `Branch`)
3. Recursively generating text by calling `generate()` on each node
4. Using `CharPredicate` interfaces (JDK 21+) to determine valid characters for character classes

This approach works with JDK 21+ which uses `CharPredicate`-based implementations for character matching.

## Compatibility

- **JDK 21+**: Fully supported (uses CharPredicate-based internal structure)

## Limitations

- Unbounded quantifiers (`*` and `+`) are limited to `STAR_AND_PLUS_GENERATION_LIMIT` repetitions (default: 10)
- Some advanced regex features may not be fully supported
- Backreferences are not supported
- Lookahead/lookbehind assertions are not supported
- Unicode property escapes may have limited support

## Version History

- **v0.2.0**: Upgraded to Java SE 21 with module system support, removed legacy JDK 6-8 compatibility
- **v0.1.0**: Original version for J2SE 6

## License

See the project repository for license information.
