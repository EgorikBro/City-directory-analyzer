package citydirectory.input;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

public class ConsoleInput {

    private final Scanner scanner = new Scanner(System.in);
    private final String exitCommand;

    public ConsoleInput(String exitCommand) {
        if (exitCommand == null || exitCommand.isBlank()) {
            throw new IllegalArgumentException("Команда выхода не может быть пустой");
        }
        this.exitCommand = exitCommand;
    }

    public Optional<Path> readPath() {
        while (true) {
            System.out.print("Введите путь к файлу справочника (или «" + exitCommand + "» для выхода): ");
            if (!scanner.hasNextLine()) {
                return Optional.empty();
            }
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase(exitCommand)) {
                return Optional.empty();
            }
            line = removeQuotes(line);
            if (line.isEmpty()) {
                System.out.println("Путь не может быть пустым, попробуйте ещё раз");
                continue;
            }
            try {
                return Optional.of(Path.of(line));
            } catch (InvalidPathException e) {
                System.out.println("Некорректный путь: " + line);
            }
        }
    }

    // Проводник Windows при «Копировать как путь» берёт путь в кавычки
    private String removeQuotes(String line) {
        if (line.length() >= 2 && line.startsWith("\"") && line.endsWith("\"")) {
            return line.substring(1, line.length() - 1).trim();
        }
        return line;
    }
}
