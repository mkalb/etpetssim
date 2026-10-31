package de.mkalb.etpetssim.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses and stores command-line arguments.
 *
 * <p>Supported formats are {@code --key=value} and {@code --flag}. A value may contain whitespace; leading and trailing
 * whitespace is removed. A flag without a value or with a value that is not a recognized boolean (see
 * {@link #getBoolean(Key, boolean)}) is active. Unknown keys and syntactically invalid argument strings, including
 * arguments with an empty or blank value and non-flag keys without a value, are ignored and reported through
 * {@link AppLogger}.
 */
public final class AppArgs {

    /**
     * Regular expression pattern to match command-line arguments in the format --key=value or --flag.
     */
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("--([a-zA-Z0-9\\-]+)(=(.*))?");
    private static final int INITIAL_BUILDER_CAPACITY = 128;

    private final EnumMap<Key, String> arguments = new EnumMap<>(Key.class);

    /**
     * Creates a new instance and parses the provided command-line arguments.
     *
     * @param args the arguments to parse; may be empty
     */
    public AppArgs(String[] args) {
        for (String arg : args) {
            parseArgument(arg);
        }
    }

    /**
     * Parses a boolean value from text.
     *
     * <p>Accepted true values are {@code true}, {@code 1}, {@code yes}, and {@code on}. Accepted false values are
     * {@code false}, {@code 0}, {@code no}, and {@code off}. For all other inputs, {@code defaultValue} is returned.
     *
     * @param value        the text to parse
     * @param defaultValue the fallback value for unsupported inputs
     * @return the parsed boolean value or {@code defaultValue}
     */
    static boolean parseBooleanValue(String value, boolean defaultValue) {
        return switch (value.toLowerCase(Locale.ROOT)) {
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
     */
    public Optional<String> getValue(Key key) {
        return Optional.ofNullable(arguments.get(key));
    }

    /**
     * Returns the value for a key as a boolean.
     *
     * <p>Accepted true values are {@code true}, {@code 1}, {@code yes}, and {@code on}; accepted false values are
     * {@code false}, {@code 0}, {@code no}, and {@code off}, each ignoring case.
     *
     * @param key          the key to resolve
     * @param defaultValue the fallback value when the key is missing or cannot be parsed
     * @return the parsed value, or {@code defaultValue}
     */
    public boolean getBoolean(Key key, boolean defaultValue) {
        String value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        return parseBooleanValue(value, defaultValue);
    }

    /**
     * Returns the value for a key as an integer.
     *
     * @param key          the key to resolve
     * @param defaultValue the fallback value when the key is missing or its value is not an integer
     * @return the parsed value, or {@code defaultValue}
     */
    public int getInt(Key key, int defaultValue) {
        String value = arguments.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            AppLogger.warn("AppArgs: Invalid integer value for key '" + key.key() + "': '" + value
                    + "'. Using default value " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Returns whether a flag key is active.
     *
     * @param key the key to inspect
     * @return {@code true} if the flag is active, otherwise {@code false}
     * @throws IllegalArgumentException if {@code key} is not a flag key
     */
    public boolean isFlagActive(Key key) {
        if (!key.isFlag()) {
            throw new IllegalArgumentException("key must be a flag, but was: " + key);
        }
        return getBoolean(key, false);
    }

    /**
     * Returns whether a key was present in the parsed arguments.
     *
     * @param key the key to check
     * @return {@code true} if the key is present, otherwise {@code false}
     */
    public boolean containsKey(Key key) {
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
     * Serializes parsed arguments back into command-line form as a readable representation for logging.
     *
     * <p>Active flags are written without a value. Values that contain whitespace are enclosed in double quotes
     * without escaping, e.g. {@code --key="value with spaces"}; the result is therefore not guaranteed to be a valid
     * shell command line.
     *
     * @return arguments formatted as {@code --key=value} or {@code --flag}
     */
    public String toArgumentString() {
        StringBuilder sb = new StringBuilder(INITIAL_BUILDER_CAPACITY);
        for (Map.Entry<Key, String> entry : arguments.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append(" ");
            }
            sb.append("--").append(entry.getKey().key());
            String value = entry.getValue();
            boolean activeFlag = entry.getKey().isFlag() && parseBooleanValue(value, true);
            if (!activeFlag) {
                sb.append("=");
                if (value.chars().anyMatch(Character::isWhitespace)) {
                    sb.append('"').append(value).append('"');
                } else {
                    sb.append(value);
                }
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

    private void parseArgument(String arg) {
        Matcher matcher = ARGUMENT_PATTERN.matcher(arg);
        if (!matcher.matches() || ((matcher.group(3) != null) && matcher.group(3).isBlank())) {
            AppLogger.warn("AppArgs: Invalid argument format: '" + arg
                    + "'. Expected format is --key=value or --flag.");
            return;
        }
        Optional<Key> key = Key.fromKeyName(matcher.group(1));
        if (key.isEmpty()) {
            AppLogger.warn("AppArgs: Unknown argument: '" + matcher.group(1)
                    + "'. Please check the command-line arguments.");
            return;
        }
        Optional<String> value = Optional.ofNullable(matcher.group(3)).map(String::strip);
        if (!key.get().isFlag() && value.isEmpty()) {
            AppLogger.warn("AppArgs: Missing value for argument: '" + key.get().key()
                    + "'. Expected format is --key=value.");
            return;
        }
        if (arguments.containsKey(key.get())) {
            AppLogger.warn("AppArgs: Duplicate argument: '" + key.get().key()
                    + "'. Previous value will be overwritten.");
        }
        String storedValue = key.get().isFlag()
                ? Boolean.toString(value.map(v -> parseBooleanValue(v, true)).orElse(true))
                : value.orElseThrow();
        arguments.put(key.get(), storedValue);
    }

    /**
     * Supported command-line keys.
     *
     * <p>The declaration order defines the order of the keys in the help output.
     */
    public enum Key {
        HELP("help", AppLocalizationKeys.ARGS_HELP, true),
        LOCALE("locale", AppLocalizationKeys.ARGS_LOCALE, false),
        LOG_CONSOLE("log-console", AppLocalizationKeys.ARGS_LOG_CONSOLE, true),
        LOG_FILE("log-file", AppLocalizationKeys.ARGS_LOG_FILE, true),
        LOG_LEVEL("log-level", AppLocalizationKeys.ARGS_LOG_LEVEL, false),
        SIMULATION("simulation", AppLocalizationKeys.ARGS_SIMULATION, false);

        private final String key;
        private final String descriptionKey;
        private final boolean flag;

        Key(String key, String descriptionKey, boolean flag) {
            this.key = key;
            this.descriptionKey = descriptionKey;
            this.flag = flag;
        }

        /**
         * Resolves a key name to an enum constant.
         *
         * @param keyName the key name to resolve (case-insensitive), without the leading {@code --}
         * @return an {@link Optional} containing the matching key, or empty if unknown
         */
        public static Optional<Key> fromKeyName(String keyName) {
            return Arrays.stream(values())
                    .filter(argument -> argument.key.equalsIgnoreCase(keyName))
                    .findFirst();
        }

        /**
         * Writes a help text containing all available command-line keys.
         *
         * <p>Write errors of the target are reported through {@link AppLogger} and not rethrown.
         *
         * @param appendable the output target for the generated help text
         */
        public static void printHelp(Appendable appendable) {
            ResourceBundle bundle = AppResources.getBundle(AppLocalization.DEFAULT_LOCALE).orElseThrow();
            try {
                appendable.append(bundle.getString(AppLocalizationKeys.ARGS_HELP_TITLE))
                        .append(System.lineSeparator())
                        .append(formatHelpArguments(bundle::getString));
            } catch (IOException e) {
                AppLogger.warn("AppArgs: Failed to print help message: " + e.getMessage());
            }
        }

        /**
         * Formats the command-line arguments with descriptions from the active localization bundle.
         *
         * @return formatted command-line arguments
         * @throws IllegalStateException if localization has not been initialized
         */
        public static String formatHelpArguments() {
            return formatHelpArguments(AppLocalization::getText);
        }

        private static String formatHelpArguments(Function<String, String> getText) {
            StringBuilder help = new StringBuilder();
            for (Key key : values()) {
                help.append(String.format(Locale.ROOT, "--%s: %s%s%n",
                        key.key(),
                        getText.apply(key.descriptionKey()),
                        key.isFlag() ? " (" + getText.apply(AppLocalizationKeys.ARGS_FLAG) + ")" : ""));
            }
            return help.toString();
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
         * Returns the resource key for this argument's help description.
         *
         * @return the description resource key
         */
        public String descriptionKey() {
            return descriptionKey;
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
