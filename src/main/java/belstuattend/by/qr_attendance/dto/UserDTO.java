package belstuattend.by.qr_attendance.dto;

import java.util.List;
import java.util.Map;

public record UserDTO (String login, String fullName, String role,  String email, Map<String, List<String>> attendances){}
