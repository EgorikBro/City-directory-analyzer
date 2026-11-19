package citydirectory.model;

import java.util.List;

public class ParseResult {

    private final List<Address> addresses;
    private final int skippedLines;

    public ParseResult(List<Address> addresses, int skippedLines) {
        if (addresses == null) {
            throw new IllegalArgumentException("Список записей не может быть пустым");
        }
        if (skippedLines < 0) {
            throw new IllegalArgumentException("Число пропущенных строк не может быть отрицательным");
        }
        this.addresses = addresses;
        this.skippedLines = skippedLines;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public int getSkippedLines() {
        return skippedLines;
    }
}