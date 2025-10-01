package belstuattend.by.qr_attendance.services;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import belstuattend.by.qr_attendance.exceptions.ExcelBuilderException;
import belstuattend.by.qr_attendance.models.User;
import belstuattend.by.qr_attendance.repository.UserRepository;

@Service
public class ExcelService {
    private final UserRepository userRepository;

    @Autowired
    public ExcelService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public HSSFWorkbook getExcelAttendances(int course, String discipline) {
        List<User> students = userRepository.findAll();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(discipline);

        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("dd.MM.yy.HH.mm");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yy");

        User mostAttendancesStudent = null;
        int maxCount = -1;

        for (User student : students) {
            List<String> studentVisits = student.getAttendances()
                    .getOrDefault(discipline, Collections.emptyList());
            if (studentVisits.size() > maxCount) {
                maxCount = studentVisits.size();
                mostAttendancesStudent = student;
            }
        }

        if (mostAttendancesStudent == null) {
            return workbook;
        }

        List<String> datesOfLectures = mostAttendancesStudent.getAttendances()
                .getOrDefault(discipline, Collections.emptyList());

        Set<String> formattedDatesOfLectures = datesOfLectures.stream()
                .map(d -> LocalDateTime.parse(d, fullFormatter).format(dayFormatter))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // структура посещений
        HSSFRow header = sheet.createRow(0);
        header.createCell(0).setCellValue("ФИО");
        int col = 1;
        for (String day : formattedDatesOfLectures) {
            header.createCell(col++).setCellValue(day);
        }

        int rowNum = 1;
        for (User student : students) {
            HSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.getFullName());
            // временный костыль, надо учитывать случай нескольких лекций в день
            List<String> studentVisits = student.getAttendances()
                    .getOrDefault(discipline, Collections.emptyList())
                    .stream()
                    .map(d -> LocalDateTime.parse(d, fullFormatter).format(dayFormatter))
                    .toList();

            col = 1;
            for (String day : formattedDatesOfLectures) {
                row.createCell(col++).setCellValue(studentVisits.contains(day) ? 1 : 0);
            }

        }

        for(int i = 0; i< header.getLastCellNum(); ++i){
            sheet.autoSizeColumn(i);
        }

        return workbook;

    }

    private static void writeWorkbook(HSSFWorkbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            throw new ExcelBuilderException(e.getMessage());
        }
    }
}
