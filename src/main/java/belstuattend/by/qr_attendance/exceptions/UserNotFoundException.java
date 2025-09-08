package belstuattend.by.qr_attendance.exceptions;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message){
        super(message);
    }

}
