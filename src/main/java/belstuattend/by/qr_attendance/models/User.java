package belstuattend.by.qr_attendance.models;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String login;
    private String password;
    private String fullName;
    private String email;

    private int course;
    private int group;
    
    private String role;
    // Формат предмет : массив дат?
    // Подумать над заменой на LocalDateTime
    private Map<String, List<String>> attendances;

}
