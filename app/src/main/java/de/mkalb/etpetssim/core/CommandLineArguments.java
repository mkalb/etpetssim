package de.mkalb.etpetssim.core;

import java.util.*;
import java.util.regex.*;

/**
 * Parses and stores command-line arguments in the format --key=value or --flag.
 */
public final class CommandLineArguments {

    /**
     * Regular expression pattern to match command-line arguments in the format --key=value or --flag.
     */
    private static final String ARGUMENTS_PATTERN = "--([a-zA-Z0-9\\-]+)(=(\\S+))?";
    /**
     * Default value for boolean flags when no value is provided.
     */
    private static final String FLAG_VALUE = Boolean.TRUE.toString();

    /**
     * Map to store parsed command-line arguments.
     */
    private final EnumMap<Key, String> arguments = new EnumMap<>(Key.class);

    /**
     * Supported command-line keys.
     */
    public enum Key {
        HELP("help", "Shows this help message", true),
        SIMULATION("simulation", "Starts the specified simulation. Format: --simulation=<name>", false);

        private final String key;
        private final String description;
        private final boolean flag;

        Key(String key, String description, boolean flag) {
            this.key = key;
            this.description = description;
            this.flag = flag;
        }

        public String key() {
            return key;
        }

        public String description() {
            return description;
        }

        public boolean isFlag() {
            return flag;
        }

        /**
         * Returns the Key enum constant corresponding to the given string, ignoring case.
         *
         * @param key the string representation of the key
         * @return an Optional containing the Key if found, or an empty Optional if not found
         */
        public static Optional<Key> fromString(String key) {
            return Arrays.stream(values())
                         .filter(argument -> argument.key.equalsIgnoreCase(key))
                         .findFirst();
        }

    }

    /**
     * Constructs a new CommandLineArguments instance and parses the given arguments.
     *
     * @param args the command-line arguments to parse. Must not be null. Can be empty.
     */
    public CommandLineArguments(String[] args) {
        Objects.requireNonNull(args, "Arguments must not be null");
        if (args.length == 0) {
            return; // No arguments to parse.
        }

        Pattern pattern = Pattern.compile(ARGUMENTS_PATTERN);
        for (String arg : args) {
            Matcher matcher = pattern.matcher(arg);
            if (matcher.matches()) {
                String keyStr = matcher.group(1);
                String value = (matcher.group(3) != null) ? matcher.group(3) : FLAG_VALUE;

                Key.fromString(keyStr).ifPresentOrElse(
                        key -> arguments.put(key, value),
                        () -> System.err.printf("Warning: Unknown argument '%s'. Ignored.%n", arg)
                );
            } else {
                System.err.printf("Warning: Argument has invalid format: '%s'. Expected format is --key=value or --flag.%n", arg);
            }
        }
    }

    public Optional<String> getValue(Key key) {
        Objects.requireNonNull(key);
        return Optional.ofNullable(arguments.get(key));
    }

    /**
     * Returns the boolean value of the given key.
     * If the key is not present or the value is not a valid boolean, the default is returned.
     *
     * @param key          the key to look up
     * @param defaultValue the value to return if the key is missing or invalid
     * @return the parsed boolean value or the default
     */
    public boolean getBoolean(Key key, boolean defaultValue) {
        Objects.requireNonNull(key);
        String value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        return switch (value.toLowerCase()) {
            case "true", "1", "yes", "on" -> true;
            case "false", "0", "no", "off" -> false;
            default -> defaultValue;
        };
    }

    /**
     * Returns the integer value of the given key.
     * If the key is not present or the value is not a valid integer, the default is returned.
     *
     * @param key          the key to look up
     * @param defaultValue the value to return if the key is missing or invalid
     * @return the parsed integer value or the default
     */
    public int getInt(Key key, int defaultValue) {
        Objects.requireNonNull(key);
        String value = arguments.get(key);
        if ((value == null) || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.printf("Warning: Invalid integer value for key '%s': '%s'. Using default value %d.%n",
                    key.key(),
                    value,
                    defaultValue);
            return defaultValue;
        }
    }

    public boolean hasKey(Key key) {
        Objects.requireNonNull(key);
        return arguments.containsKey(key);
    }

    public Set<Key> keys() {
        return Collections.unmodifiableSet(arguments.keySet());
    }

    public void printHelp() {
        System.out.println("List of available command-line arguments:");
        for (Key key : Key.values()) {
            System.out.printf("--%s: %s%s%n",
                    key.key(),
                    key.description(),
                    key.isFlag() ? " (flag)" : "");
        }
    }

}

