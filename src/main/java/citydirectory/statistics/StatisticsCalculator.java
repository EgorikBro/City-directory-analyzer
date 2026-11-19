package citydirectory.statistics;

import citydirectory.model.Address;
import citydirectory.model.CityStatistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsCalculator {

    private final int maxFloors;

    public StatisticsCalculator(int maxFloors) {
        if (maxFloors <= 0) {
            throw new IllegalArgumentException("Максимальная этажность должна быть больше нуля");
        }
        this.maxFloors = maxFloors;
    }

    public CityStatistics calculate(List<Address> addresses) {
        if (addresses == null) {
            throw new IllegalArgumentException("Список записей не может быть пустым");
        }

        Map<Address, Integer> counts = HashMap.newHashMap(addresses.size());
        for (Address address : addresses) {
            counts.merge(address, 1, Integer::sum);
        }

        Map<Address, Integer> duplicates = new HashMap<>();
        Map<String, int[]> floorsByCity = new HashMap<>();

        for (Map.Entry<Address, Integer> entry : counts.entrySet()) {
            Address address = entry.getKey();
            if (entry.getValue() > 1) {
                duplicates.put(address, entry.getValue());
            }
            int floor = address.getFloor();
            if (floor <= maxFloors) {
                // Индекс 0 — одноэтажные дома, индекс maxFloors - 1 — самые высокие
                floorsByCity.computeIfAbsent(address.getCity(), city -> new int[maxFloors])[floor - 1]++;
            }
        }
        return new CityStatistics(duplicates, floorsByCity, maxFloors, addresses.size());
    }
}
