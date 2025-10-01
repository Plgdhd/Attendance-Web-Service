package belstuattend.by.qr_attendance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String login;
    private String fullName;
    private String role;
    private String email;
    private Map<String, List<String>> attendances;
}