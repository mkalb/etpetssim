package de.mkalb.etpetssim.core;

import java.util.*;
import java.util.regex.*;

/**
 * Parses and stores command-line arguments.
 * <p>
 * Supported formats are {@code --key=value} and {@code --flag}. Unknown keys and
 * malformed arguments are ignored and reported through {@link AppLogger}.
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
     * Creates a new instance and parses the provided command-line arguments.
     *
     * @param args the arguments to parse; may be empty
     * @throws NullPointerException if {@code args} is {@code null}
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
     * Parses a boolean value from text.
     * <p>
     * Accepted true values are {@code true}, {@code 1}, {@code yes}, and {@code on}.
     * Accepted false values are {@code false}, {@code 0}, {@code no}, and {@code off}.
     * For all other inputs, {@code defaultValue} is returned.
     *
     * @param value the text to parse
     * @param defaultValue the fallback value for unsupported inputs
     * @return the parsed boolean value or {@code defaultValue}
     * @throws NullPointerException if {@code value} is {@code null}
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
     * Returns the raw string value for a key.
     *
     * @param key the key to resolve
     * @return an {@link Optional} with the stored value, or empty if the key is absent
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public Optional<String> getValue(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
        return Optional.ofNullable(arguments.get(key));
    }

    /**
     * Returns the value for a key as a boolean.
     *
     * @param key the key to resolve
     * @param defaultValue the fallback value when the key is missing or cannot be parsed
     * @return the parsed value, or {@code defaultValue}
     * @throws NullPointerException if {@code key} is {@code null}
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
     * Returns the value for a key as an integer.
     *
     * @param key the key to resolve
     * @param defaultValue the fallback value when the key is missing, blank, or invalid
     * @return the parsed value, or {@code defaultValue}
     * @throws NullPointerException if {@code key} is {@code null}
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
     * Returns whether a flag key is active.
     *
     * @param key the key to inspect
     * @return {@code true} if the flag is active, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     * @throws IllegalArgumentException if {@code key} is not a flag key
     */
    public boolean isFlagActive(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
        if (!key.isFlag()) {
            throw new IllegalArgumentException("Key is not a flag: " + key);
        }
        return getBoolean(key, false);
    }

    /**
     * Returns whether a key was present in the parsed arguments.
     *
     * @param key the key to check
     * @return {@code true} if the key is present, otherwise {@code false}
     * @throws NullPointerException if {@code key} is {@code null}
     */
    public boolean hasKey(Key key) {
        Objects.requireNonNull(key, "Key must not be null");
        return arguments.containsKey(key);
    }

    /**
     * Returns an unmodifiable view of all present keys.
     *
     * @return an unmodifiable set of parsed keys
     */
    public Set<Key> keys() {
        return Collections.unmodifiableSet(arguments.keySet());
    }

    /**
     * Serializes parsed arguments back into command-line form.
     *
     * @return arguments formatted as {@code --key=value} or {@code --flag}
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
         * Resolves a key identifier to an enum constant.
         *
         * @param key the key name to resolve (case-insensitive)
         * @return an {@link Optional} containing the matching key, or empty if unknown
         * @throws NullPointerException if {@code key} is {@code null}
         */
        public static Optional<Key> fromString(String key) {
            Objects.requireNonNull(key, "Key must not be null");
            return Arrays.stream(values())
                         .filter(argument -> argument.key.equalsIgnoreCase(key))
                         .findFirst();
        }

        /**
         * Writes a help text containing all available command-line keys.
         *
         * @param appendable the output target for the generated help text
         * @throws NullPointerException if {@code appendable} is {@code null}
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
         * Returns the command-line key name.
         *
         * @return the key name without the leading {@code --}
         */
        public String key() {
            return key;
        }

        /**
         * Returns the help description.
         *
         * @return the key description shown in help output
         */
        public String description() {
            return description;
        }

        /**
         * Returns whether this key represents a flag.
         *
         * @return {@code true} if the key is a flag, otherwise {@code false}
         */
        public boolean isFlag() {
            return flag;
        }

    }

}

