package citydirectory.parser;

import citydirectory.exception.DirectoryFileException;
import citydirectory.model.Address;
import citydirectory.model.ParseResult;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class XmlAddressParser implements AddressParser {

    private final XMLInputFactory factory;

    public XmlAddressParser() {
        factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    // StAX читает файл по одному элементу, не загружая его целиком в память
    @Override
    public ParseResult parse(Path file) {
        List<Address> addresses = new ArrayList<>();
        int skippedLines = 0;

        try (InputStream input = new BufferedInputStream(Files.newInputStream(file))) {
            XMLStreamReader reader = factory.createXMLStreamReader(input);
            try {
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT && reader.getAttributeValue(null, "city") != null) {
                        Address address = parseElement(reader);
                        if (address != null) {
                            addresses.add(address);
                        } else {
                            skippedLines++;
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (NoSuchFileException e) {
            throw new DirectoryFileException("Файл не найден: " + file);
        } catch (AccessDeniedException e) {
            throw new DirectoryFileException("Нет доступа к файлу: " + file);
        } catch (XMLStreamException e) {
            throw new DirectoryFileException("Файл повреждён или не является корректным XML: " + file
                    + lineInfo(e));
        } catch (IOException e) {
            throw new DirectoryFileException("Не удалось прочитать файл: " + file, e);
        }
        return new ParseResult(addresses, skippedLines);
    }

    // Возвращает null, если у элемента не хватает атрибутов или они некорректны
    private Address parseElement(XMLStreamReader reader) {
        String city = reader.getAttributeValue(null, "city");
        String street = reader.getAttributeValue(null, "street");
        String house = reader.getAttributeValue(null, "house");
        String floor = reader.getAttributeValue(null, "floor");
        if (street == null || house == null || floor == null) {
            return null;
        }
        try {
            return new Address(city.trim(), street.trim(), house.trim(), Integer.parseInt(floor.trim()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String lineInfo(XMLStreamException e) {
        if (e.getLocation() == null || e.getLocation().getLineNumber() < 0) {
            return "";
        }
        return " (строка " + e.getLocation().getLineNumber() + ")";
    }
}