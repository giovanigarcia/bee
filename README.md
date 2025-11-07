bee
===

Generates text matching Regular Expressions

It might be useful, not without tweaking.

## Requirements

- Java SE 9 or higher (tested with Java 21)
- The application uses reflection to access internal `java.util.regex.Pattern` classes

## Building

```bash
./gradlew build
```

## Running

```bash
./gradlew run --args="<regex_file>"
```

Where `<regex_file>` contains one regular expression per line.

**Note**: The `--add-opens java.base/java.util.regex=ALL-UNNAMED` JVM flag is required to access internal Pattern classes via reflection. The Gradle build configuration includes this flag automatically.

## Example

Create a file `patterns.txt`:
```
[a-z]{3,5}
\d{2,4}
hello|world
```

Run:
```bash
./gradlew run --args="patterns.txt"
```

## Compatibility

- **JDK 9 - 25**: Fully supported (uses new CharPredicate-based internal structure)
- **JDK 6 - 8**: Legacy support retained in code (uses old BitClass/Single structure)

## Version History

- **v2.0**: Upgraded to Java SE 9+ with module system support
- **v1.0**: Original version for J2SE 6
