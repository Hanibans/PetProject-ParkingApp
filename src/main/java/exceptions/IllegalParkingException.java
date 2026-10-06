package exceptions;

public class IllegalParkingException extends RuntimeException {
    public IllegalParkingException(String message) {
        super(message);
    }
}
