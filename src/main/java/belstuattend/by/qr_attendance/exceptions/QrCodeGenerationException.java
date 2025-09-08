package belstuattend.by.qr_attendance.exceptions;

public class QrCodeGenerationException extends RuntimeException{
    
    public QrCodeGenerationException(String message){
        super(message);
    }
}
