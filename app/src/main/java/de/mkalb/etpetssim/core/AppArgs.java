package de.mkalb.etpetssim.core;

import java.util.*;
import java.util.regex.*;

/**
 * Parses and stores command-line arguments in the format --key=value or --flag.
 */
public final class AppArgs {

    /**
     * Regular expression pattern to match command-line arguments in the format --key=value or --flag.
     */
    private static final String ARGUMENT_PATTERN = "--([a-zA-Z0-9\\-]+)(=(\\S+))?";
    private static final int INITIAL_BUILDER_CAPACITY = 128; // Initial capacity for the StringBuilder used to build the arguments string

    /**
     * Map to store parsed command-line arguments.
     */
    private final EnumMap<Key, String> arguments = new EnumMap<>(Key.class);

    /**
     * Constructs a new AppArgs instance and parses the given arguments.
     *
     * @param args the command-line arguments to parse. Must not be null. Can be empty.
     */
    public AppArgs(String[] args) {
        Objects.requireNonNull(args, "Arguments must not be null");
        if (args.length > 0) {
            Pattern pattern = Pattern.compile(ARGUMENT_PATTERN);
            for (String arg : args) {
                Matcher matcher = pattern.matcher(arg);
                if (matcher.matches()) {
                    Optional<Key> key = Key.fromString(matcher.group(1));
                    if (key.isPresent()) {
                        if (arguments.containsKey(key.get())) {
                            AppLogger.warn("AppArgs: Duplicate argument: '" + key.get().key() + "'. Previous value will be overwritten.");
                        }
                        Optional<String> value = (matcher.group(3) != null) ? Optional.of(matcher.group(3)) : Optional.empty();
                        if (key.get().isFlag()) {
                            // For flag keys, we parse the value as a boolean or set it to true if no value is provided.
                            arguments.put(key.get(),
                                    value.map(v -> parseBooleanValue(v, Boolean.TRUE))
                                         .orElse(Boolean.TRUE)
                                         .toString());
                        } else {
                            // For non-flag keys, we put the value only if it is present.
                            value.ifPresent(v -> arguments.put(key.get(), v));
                        }
                    } else {
                        AppLogger.warn("AppArgs: Unknown argument: '" + matcher.group(1) + "'. Please check the command-line arguments.");
                    }
                } else {
                    AppLogger.warn("AppArgs: Invalid argument format: '" + arg + "'. Expected format is --key=value or --flag.");
                }
            }
        }
    }

    /**
     * Parses a boolean value from the given string.
     * Boolean values can be represented as "true", "false", "1", "0", "yes", "no", "on", or "off".
     *
     * @param value        the string to parse
     * @param defaultValue the default value to return if the string is not a valid boolean
     * @return the parsed boolean value or the defaultValue
     */
    static boolean parseBooleanValue(String value, boolean defaultValue) {
        Objects.requireNonNull(value, "Value must not be null");
        return switch (value.toLowerCase()) {
            case "true", "1", "yes", "on" -> Boolean.TRUE;
            case "false", "0", "no", "off" -> Boolean.FALSE;
            default -> defaultValue;
        };
    }

    /**
     * Returns the value associated with the given key.
     *
     * @param key the key to look up
     * @return an Optional containing the value if present, or an empty Optional if the key is not found
     */
    public Optional<String> getValue(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
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
        Objects.requireNonNull(key, "Key must not be null");
        String value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        return parseBooleanValue(value, defaultValue);
    }

    /**
     * Returns the integer value of the given key.
     * If the key is not present, the value is blank or the value is not a valid integer, the default is returned.
     *
     * @param key          the key to look up
     * @param defaultValue the value to return if the key is missing or invalid
     * @return the parsed integer value or the default
     */
    public int getInt(Key key, int defaultValue) {
        Objects.requireNonNull(key, "Key must not be null");
        String value = arguments.get(key);
        if ((value == null) || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            AppLogger.warn("AppArgs: Invalid integer value for key '" + key.key() + "': '" + value + "'. Using default value " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Checks if the given key is a flag and returns its active state.
     *
     * @param key the key to check
     * @return true if the flag is active, false otherwise
     * @throws IllegalArgumentException if the key is not a flag
     */
    public boolean isFlagActive(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
        if (!key.isFlag()) {
            throw new IllegalArgumentException("Key is not a flag: " + key);
        }
        return getBoolean(key, false);
    }

    /**
     * Checks if the given key exists in the command-line arguments.
     *
     * @param key the key to check
     * @return true if the key exists, false otherwise
     */
    public boolean hasKey(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
        return arguments.containsKey(key);
    }

    /**
     * Returns an unmodifiable set of all keys present in the command-line arguments.
     *
     * @return an unmodifiable set of keys
     */
    public Set<Key> keys() {
        return Collections.unmodifiableSet(arguments.keySet());
    }

    /**
     * Returns a string representation of all command-line arguments.
     *
     * @return a string containing all arguments in the format --key=value or --flag
     */
    public String argumentsAsString() {
        StringBuilder sb = new StringBuilder(INITIAL_BUILDER_CAPACITY);
        for (Map.Entry<Key, String> entry : arguments.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append("--").append(entry.getKey().key());
            if (!entry.getKey().isFlag() // is not a flag
                    || !parseBooleanValue(entry.getValue(), true)) { // or flag is false
                sb.append("=").append(entry.getValue());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "AppArgs{" +
                "arguments=" + arguments +
                '}';
    }

    /**
     * Supported command-line keys.
     */
    public enum Key {
        HELP("help", "Shows this help message", true),
        LOCALE("locale", "Sets the locale for the simulation. Format: --locale=<language>_<country> or --locale=<language>", false),
        LOG_CONSOLE("log-console", "Enables logging to the console. Format: --log-console", true),
        LOG_FILE("log-file", "Enables logging to a file. Format: --log-file", true),
        LOG_LEVEL("log-level", "Sets the log level. Format: --log-level=<level>", false),
        SIMULATION("simulation", "Starts the specified simulation. Format: --simulation=<name>", false);

        private final String key;
        private final String description;
        private final boolean flag;

        Key(String key, String description, boolean flag) {
            this.key = key;
            this.description = description;
            this.flag = flag;
        }

        /**
         * Returns the Key enum constant corresponding to the given string, ignoring case.
         *
         * @param key the string representation of the key
         * @return an Optional containing the Key if found, or an empty Optional if not found
         */
        public static Optional<Key> fromString(String key) {
            Objects.requireNonNull(key, "Key must not be null");
            return Arrays.stream(values())
                         .filter(argument -> argument.key.equalsIgnoreCase(key))
                         .findFirst();
        }

        /**
         * Prints a help message listing all available command-line arguments and their descriptions.
         *
         * @param appendable the Appendable to which the help message will be written
         */
        @SuppressWarnings("HardcodedLineSeparator")
        public static void printHelp(Appendable appendable) {
            Objects.requireNonNull(appendable, "Appendable must not be null");
            try {
                appendable.append("List of available command-line arguments:\n");
                for (Key key : values()) {
                    appendable.append(String.format("--%s: %s%s%n",
                            key.key(),
                            key.description(),
                            key.isFlag() ? " (flag)" : ""));
                }
            } catch (Exception e) {
                AppLogger.warn("AppArgs: Failed to print help message: " + e.getMessage());
            }
        }

        /**
         * Returns the key as a string.
         *
         * @return the key string
         */
        public String key() {
            return key;
        }

        /**
         * Returns the description of the key.
         *
         * @return the description string
         */
        public String description() {
            return description;
        }

        /**
         * Returns whether this key is a flag (i.e., does not require a value).
         *
         * @return true if this key is a flag, false otherwise
         */
        public boolean isFlag() {
            return flag;
        }

    }

}

