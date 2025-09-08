package belstuattend.by.qr_attendance.exceptions;

public class WrongCodeException extends RuntimeException{

    public WrongCodeException(String message){
        super(message);
    }
}
