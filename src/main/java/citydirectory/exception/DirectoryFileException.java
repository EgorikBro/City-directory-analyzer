package citydirectory.exception;

public class DirectoryFileException extends RuntimeException {

    public DirectoryFileException(String message) {
        super(message);
    }

    public DirectoryFileException(String message, Throwable cause) {
        super(message, cause);
    }
}