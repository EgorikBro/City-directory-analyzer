package citydirectory.output;

import citydirectory.model.Address;
import citydirectory.model.CityStatistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StatisticsPrinter {

    private static final String CITY_HEADER = "Город";

    public void printWelcome() {
        System.out.println("===== Анализ справочника городов =====");
    }

    public void printGoodbye() {
        System.out.println("До свидания!");
    }

    public void printError(String message) {
        System.out.println("Ошибка: " + message);
    }

    public void printStatistics(CityStatistics statistics, int skippedLines, long elapsedMillis) {
        System.out.println();
        printDuplicates(statistics);
        System.out.println();
        printFloors(statistics);
        System.out.println();
        printSummary(statistics.getTotalRecords(), skippedLines, elapsedMillis);
        System.out.println();
    }

    private void printDuplicates(CityStatistics statistics) {
        Map<Address, Integer> duplicates = statistics.getDuplicates();
        if (duplicates.isEmpty()) {
            System.out.println("Дублирующихся записей нет");
            return;
        }
        System.out.println("Дублирующиеся записи (" + duplicates.size() + "):");
        // Сначала записи, которые повторяются чаще всего
        List<Map.Entry<Address, Integer>> entries = new ArrayList<>(duplicates.entrySet());
        entries.sort((first, second) -> second.getValue() - first.getValue());
        for (Map.Entry<Address, Integer> entry : entries) {
            System.out.println("  " + entry.getKey() + " — повторений: " + entry.getValue());
        }
    }

    private void printFloors(CityStatistics statistics) {
        List<String> cities = statistics.getCities();
        if (cities.isEmpty()) {
            System.out.println("В файле нет зданий для подсчёта этажности");
            return;
        }

        int cityWidth = CITY_HEADER.length();
        for (String city : cities) {
            cityWidth = Math.max(cityWidth, city.length());
        }
        String cityFormat = "%-" + cityWidth + "s";

        System.out.println("Количество зданий по этажности:");
        StringBuilder header = new StringBuilder(String.format(cityFormat, CITY_HEADER));
        for (int floors = 1; floors <= statistics.getMaxFloors(); floors++) {
            header.append(String.format("%9s", floors + " эт."));
        }
        System.out.println(header);

        for (String city : cities) {
            StringBuilder row = new StringBuilder(String.format(cityFormat, city));
            for (int floors = 1; floors <= statistics.getMaxFloors(); floors++) {
                row.append(String.format("%9d", statistics.getBuildingCount(city, floors)));
            }
            System.out.println(row);
        }
    }

    private void printSummary(int totalRecords, int skippedLines, long elapsedMillis) {
        System.out.println("Всего записей: " + totalRecords);
        if (skippedLines > 0) {
            System.out.println("Пропущено некорректных записей: " + skippedLines);
        }
        System.out.println(String.format("Время обработки файла: %.3f с", elapsedMillis / 1000.0));
    }
}
