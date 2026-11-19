package citydirectory.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CityStatistics {

    private final Map<Address, Integer> duplicates;
    private final Map<String, int[]> floorsByCity;
    private final int maxFloors;
    private final int totalRecords;

    public CityStatistics(Map<Address, Integer> duplicates, Map<String, int[]> floorsByCity,
                          int maxFloors, int totalRecords) {
        if (duplicates == null || floorsByCity == null) {
            throw new IllegalArgumentException("Данные статистики не могут быть пустыми");
        }
        if (maxFloors <= 0) {
            throw new IllegalArgumentException("Максимальная этажность должна быть больше нуля");
        }
        this.duplicates = duplicates;
        this.floorsByCity = floorsByCity;
        this.maxFloors = maxFloors;
        this.totalRecords = totalRecords;
    }

    // Только записи, которые встречаются больше одного раза, со значением — сколько раз
    public Map<Address, Integer> getDuplicates() {
        return Collections.unmodifiableMap(duplicates);
    }

    public List<String> getCities() {
        List<String> cities = new ArrayList<>(floorsByCity.keySet());
        Collections.sort(cities);
        return cities;
    }

    public int getBuildingCount(String city, int floors) {
        if (floors < 1 || floors > maxFloors) {
            throw new IllegalArgumentException("Этажность должна быть от 1 до " + maxFloors);
        }
        int[] counts = floorsByCity.get(city);
        if (counts == null) {
            return 0;
        }
        return counts[floors - 1];
    }

    public int getMaxFloors() {
        return maxFloors;
    }

    public int getTotalRecords() {
        return totalRecords;
    }
}
