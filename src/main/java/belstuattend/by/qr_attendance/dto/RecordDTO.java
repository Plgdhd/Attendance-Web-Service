package belstuattend.by.qr_attendance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordDTO {
    private String discipline;
    private String code;
}