package citydirectory.parser;

import citydirectory.exception.DirectoryFileException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Подбирает парсер по расширению файла. Новый формат добавляется одной строкой register в Main
public class ParserRegistry {

    private final Map<String, AddressParser> parsers = new HashMap<>();

    public void register(String extension, AddressParser parser) {
        if (extension == null || extension.isBlank()) {
            throw new IllegalArgumentException("Расширение не может быть пустым");
        }
        if (parser == null) {
            throw new IllegalArgumentException("Парсер не может быть пустым");
        }
        parsers.put(extension.toLowerCase(Locale.ROOT), parser);
    }

    public AddressParser getParser(Path file) {
        String extension = getExtension(file);
        AddressParser parser = parsers.get(extension);
        if (parser == null) {
            throw new DirectoryFileException("Неподдерживаемый формат файла: " + file
                    + ". Поддерживаются: " + String.join(", ", parsers.keySet()));
        }
        return parser;
    }

    private String getExtension(Path file) {
        Path fileName = file.getFileName();
        if (fileName == null) {
            return "";
        }
        String name = fileName.toString();
        int dot = name.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return name.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}