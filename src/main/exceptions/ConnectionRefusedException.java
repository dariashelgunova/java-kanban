package main.exceptions;

public class ConnectionRefusedException extends RuntimeException {
    public ConnectionRefusedException(String message) {
        super(message);
    }
}
