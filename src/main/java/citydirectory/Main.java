package citydirectory;

import citydirectory.app.CityDirectoryApp;
import citydirectory.config.AppConfig;
import citydirectory.input.ConsoleInput;
import citydirectory.output.StatisticsPrinter;
import citydirectory.parser.CsvAddressParser;
import citydirectory.parser.ParserRegistry;
import citydirectory.parser.XmlAddressParser;
import citydirectory.statistics.StatisticsCalculator;

public class Main {

    public static void main(String[] args) {
        CityDirectoryApp app;
        try {
            AppConfig config = new AppConfig();

            ParserRegistry parsers = new ParserRegistry();
            parsers.register("csv", new CsvAddressParser(config.getCsvSeparator()));
            parsers.register("xml", new XmlAddressParser());

            app = new CityDirectoryApp(
                    new ConsoleInput(config.getExitCommand()),
                    parsers,
                    new StatisticsCalculator(config.getMaxFloors()),
                    new StatisticsPrinter()
            );
        } catch (IllegalStateException e) {
            System.out.println("Ошибка настроек: " + e.getMessage());
            return;
        }
        app.run();
    }
}