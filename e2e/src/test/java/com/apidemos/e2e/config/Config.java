package com.apidemos.e2e.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Reads config/config.properties from the test classpath.
 * A JVM system property of the same name always wins, so any value can be
 * overridden on the command line: {@code mvn test -Ddevice.name=emulator-5556}
 */
public final class Config {

    private static final Properties PROPS = load();

    private Config() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader()
                .getResourceAsStream("config/config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "config/config.properties not found on the test classpath");
            }
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read config/config.properties", e);
        }
        return props;
    }

    /** System property first, then config.properties. Never null; blank if unset. */
    public static String get(String key) {
        String override = System.getProperty(key);
        if (override != null && !override.isBlank()) {
            return override.trim();
        }
        String value = PROPS.getProperty(key);
        return value == null ? "" : value.trim();
    }

    public static String require(String key) {
        String value = get(key);
        if (value.isBlank()) {
            throw new IllegalStateException("Required property '" + key + "' is not set");
        }
        return value;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    /**
     * Boolean with an explicit default. Use this for switches whose safe value
     * is {@code true} - plain getBoolean would turn a missing or misspelled key
     * into a silent {@code false}.
     */
    public static boolean getBoolean(String key, boolean fallback) {
        String value = get(key);
        return value.isBlank() ? fallback : Boolean.parseBoolean(value);
    }

    public static Duration getSeconds(String key) {
        return Duration.ofSeconds(Long.parseLong(require(key)));
    }
}
