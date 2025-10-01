package belstuattend.by.qr_attendance.controllers;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import belstuattend.by.qr_attendance.services.ExcelService;

@RestController
@RequestMapping("/api/attendance")
public class ExcelController {
    
    private final ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService){ 
        this.excelService = excelService;
    }

    @GetMapping("/attendances/{course}")
    public ResponseEntity<HSSFWorkbook> getExcelAttendances(@PathVariable int course){
        return ResponseEntity.ok().body(excelService.getExcelAttendances(2, "Основы Алгоритмизации и программирования"));
    }
    

}
