package citydirectory.app;

import citydirectory.exception.DirectoryFileException;
import citydirectory.input.ConsoleInput;
import citydirectory.model.CityStatistics;
import citydirectory.model.ParseResult;
import citydirectory.output.StatisticsPrinter;
import citydirectory.parser.AddressParser;
import citydirectory.parser.ParserRegistry;
import citydirectory.statistics.StatisticsCalculator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class CityDirectoryApp {

    private final ConsoleInput input;
    private final ParserRegistry parsers;
    private final StatisticsCalculator calculator;
    private final StatisticsPrinter printer;

    public CityDirectoryApp(ConsoleInput input, ParserRegistry parsers,
                            StatisticsCalculator calculator, StatisticsPrinter printer) {
        this.input = input;
        this.parsers = parsers;
        this.calculator = calculator;
        this.printer = printer;
    }

    public void run() {
        printer.printWelcome();
        while (true) {
            Optional<Path> file = input.readPath();
            if (file.isEmpty()) {
                printer.printGoodbye();
                return;
            }
            try {
                processFile(file.get());
            } catch (DirectoryFileException e) {
                printer.printError(e.getMessage());
            } catch (OutOfMemoryError e) {
                printer.printError("Не хватило памяти для обработки файла: " + file.get());
            }
        }
    }

    private void processFile(Path file) {
        checkFile(file);
        AddressParser parser = parsers.getParser(file);

        long start = System.nanoTime();
        ParseResult result = parser.parse(file);
        if (result.getAddresses().isEmpty()) {
            throw new DirectoryFileException("В файле не найдено ни одной записи справочника: " + file.toAbsolutePath());
        }
        CityStatistics statistics = calculator.calculate(result.getAddresses());
        long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

        printer.printStatistics(statistics, result.getSkippedLines(), elapsedMillis);
    }

    private void checkFile(Path file) {
        if (!Files.exists(file)) {
            throw new DirectoryFileException("Файл не найден: " + file.toAbsolutePath());
        }
        if (Files.isDirectory(file)) {
            throw new DirectoryFileException("Указана папка, а нужен файл: " + file.toAbsolutePath());
        }
        if (!Files.isReadable(file)) {
            throw new DirectoryFileException("Нет доступа к файлу: " + file.toAbsolutePath());
        }
    }
}