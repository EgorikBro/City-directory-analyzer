package citydirectory.parser;

import citydirectory.model.ParseResult;

import java.nio.file.Path;

// Общий контракт для чтения справочника: остальной программе неважно, в каком формате файл
public interface AddressParser {

    // Бросает DirectoryFileException, если файл нельзя прочитать целиком
    ParseResult parse(Path file);
}