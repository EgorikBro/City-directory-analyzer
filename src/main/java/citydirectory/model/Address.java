package citydirectory.model;

import java.util.Objects;

public class Address {

    private final String city;
    private final String street;
    private final String house;
    private final int floor;
    private final int hash;

    public Address(String city, String street, String house, int floor) {
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("Город не может быть пустым");
        }
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Улица не может быть пустой");
        }
        if (house == null || house.isBlank()) {
            throw new IllegalArgumentException("Номер дома не может быть пустым");
        }
        if (floor <= 0) {
            throw new IllegalArgumentException("Этажность должна быть больше нуля: " + floor);
        }
        this.city = city;
        this.street = street;
        this.house = house;
        this.floor = floor;
        // Хеш считается один раз: при подсчёте дублей HashMap запрашивает его для каждой из миллионов записей
        this.hash = Objects.hash(city, street, house, floor);
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    public int getFloor() {
        return floor;
    }

    // equals и hashCode нужны, чтобы одинаковые записи попадали в один ключ HashMap при поиске дублей
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        Address other = (Address) o;
        return floor == other.floor
                && city.equals(other.city)
                && street.equals(other.street)
                && house.equals(other.house);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return city + ", " + street + ", д. " + house + ", этажей: " + floor;
    }
}
