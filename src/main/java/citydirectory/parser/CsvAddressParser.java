package citydirectory.parser;

import citydirectory.exception.DirectoryFileException;
import citydirectory.model.Address;
import citydirectory.model.ParseResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CsvAddressParser implements AddressParser {

    private static final int FIELD_COUNT = 4;
    private static final char BOM = '\uFEFF';

    private final Pattern separator;

    public CsvAddressParser(String separator) {
        if (separator == null || separator.isEmpty()) {
            throw new IllegalArgumentException("Разделитель не может быть пустым");
        }
        this.separator = Pattern.compile(Pattern.quote(separator));
    }

    @Override
    public ParseResult parse(Path file) {
        List<Address> addresses = new ArrayList<>();
        int skippedLines = 0;
        boolean firstLine = true;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (firstLine && !line.isEmpty() && line.charAt(0) == BOM) {
                    line = line.substring(1);
                }
                if (line.isBlank()) {
                    continue;
                }
                Address address = parseLine(line);
                if (address != null) {
                    addresses.add(address);
                } else if (!firstLine) {
                    // Первая строка, которая не разобралась - это заголовок, его не считаем ошибкой
                    skippedLines++;
                }
                firstLine = false;
            }
        } catch (NoSuchFileException e) {
            throw new DirectoryFileException("Файл не найден: " + file);
        } catch (AccessDeniedException e) {
            throw new DirectoryFileException("Нет доступа к файлу: " + file);
        } catch (MalformedInputException e) {
            throw new DirectoryFileException("Файл должен быть в кодировке UTF-8: " + file);
        } catch (IOException e) {
            throw new DirectoryFileException("Не удалось прочитать файл: " + file, e);
        }
        return new ParseResult(addresses, skippedLines);
    }

    // Возвращает null, если строка не похожа на запись справочника
    private Address parseLine(String line) {
        String[] fields = separator.split(line, -1);
        if (fields.length != FIELD_COUNT) {
            return null;
        }
        try {
            return new Address(unquote(fields[0]), unquote(fields[1]), unquote(fields[2]),
                    Integer.parseInt(unquote(fields[3])));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String unquote(String field) {
        String value = field.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1).replace("\"\"", "\"");
        }
        return value.trim();
    }
}