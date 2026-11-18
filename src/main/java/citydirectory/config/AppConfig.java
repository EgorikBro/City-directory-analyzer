package citydirectory.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppConfig {

    private static final String FILE_NAME = "application.properties";

    private final String exitCommand;
    private final String csvSeparator;
    private final int maxFloors;

    // Все параметры читаются и проверяются сразу, чтобы ошибка в настройках была видна при запуске
    public AppConfig() {
        Properties properties = load();
        this.exitCommand = getString(properties, "exit.command");
        this.csvSeparator = getString(properties, "csv.separator");
        this.maxFloors = getPositiveInt(properties, "max.floors");
    }

    public String getExitCommand() {
        return exitCommand;
    }

    public String getCsvSeparator() {
        return csvSeparator;
    }

    public int getMaxFloors() {
        return maxFloors;
    }

    private Properties load() {
        InputStream stream = AppConfig.class.getClassLoader().getResourceAsStream(FILE_NAME);
        if (stream == null) {
            throw new IllegalStateException("Не найден файл настроек " + FILE_NAME);
        }
        Properties properties = new Properties();
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("Не удалось прочитать файл настроек " + FILE_NAME, e);
        }
        return properties;
    }

    private String getString(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("В файле настроек нет параметра " + key);
        }
        return value.trim();
    }

    private int getPositiveInt(Properties properties, String key) {
        String value = getString(properties, key);
        int number;
        try {
            number = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Параметр " + key + " должен быть целым числом");
        }
        if (number <= 0) {
            throw new IllegalStateException("Параметр " + key + " должен быть больше нуля");
        }
        return number;
    }
}