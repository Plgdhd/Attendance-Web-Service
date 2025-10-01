package belstuattend.by.qr_attendance.controllers;

import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.JWTVerificationException;

import belstuattend.by.qr_attendance.exceptions.DisciplineAlreadyExistsException;
import belstuattend.by.qr_attendance.exceptions.DisciplineNotFoundException;
import belstuattend.by.qr_attendance.exceptions.ExcelBuilderException;
import belstuattend.by.qr_attendance.exceptions.QrCodeGenerationException;
import belstuattend.by.qr_attendance.exceptions.RecordException;
import belstuattend.by.qr_attendance.exceptions.UserNotFoundException;
import belstuattend.by.qr_attendance.exceptions.WrongCodeException;
import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(QrCodeGenerationException.class)
    public ResponseEntity<String> handleQrCodeGenerationException(QrCodeGenerationException e) {
        return ResponseEntity.badRequest().body("Ошибка при генерации QR-кода: " + e.getMessage());
    }

    @ExceptionHandler(DisciplineNotFoundException.class)
    public ResponseEntity<String> handleDisciplineNotFoundException(DisciplineNotFoundException e) {
        return ResponseEntity.status(404).body("Дисциплина с таким именем не найдена: " + e.getMessage());
    }

    @ExceptionHandler(DisciplineAlreadyExistsException.class)
    public ResponseEntity<String> handleDisciplineAlreadyExistsException(DisciplineAlreadyExistsException e) {
        return ResponseEntity.status(404).body("Дисциплина с таким именем уже существует: " + e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException e) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Ошибка: " + e.getMessage());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<String> handleJWTVerificationException(JWTVerificationException e) {
        return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Ошибка JWT: " + e.getMessage());
    }

    @ExceptionHandler(WrongCodeException.class)
    public ResponseEntity<String> handleWrongCodeException(WrongCodeException e) {
        return ResponseEntity.badRequest().body("Проблемо: " + e.getMessage());
    }

    @ExceptionHandler(RecordException.class)
    public ResponseEntity<String> handleRecordException(RecordException e) {
        return ResponseEntity.badRequest().body("Проблемо: " + e.getMessage());
    }

    @ExceptionHandler(ExcelBuilderException.class)
    public ResponseEntity<String> handleExcelBuilderException(ExcelBuilderException e){
        return ResponseEntity.badRequest().body("Проблемо: " + e.getMessage());
    }
}
