package belstuattend.by.qr_attendance.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import belstuattend.by.qr_attendance.dto.AttendanceDTO;
import belstuattend.by.qr_attendance.dto.DisciplineDTO;
import belstuattend.by.qr_attendance.dto.RecordDTO;
import belstuattend.by.qr_attendance.dto.UserDTO;
import belstuattend.by.qr_attendance.exceptions.DisciplineNotFoundException;
import belstuattend.by.qr_attendance.exceptions.RecordException;
import belstuattend.by.qr_attendance.exceptions.WrongCodeException;
import belstuattend.by.qr_attendance.models.Discipline;
import belstuattend.by.qr_attendance.models.User;
import belstuattend.by.qr_attendance.security.JWTUtil;
import belstuattend.by.qr_attendance.services.AttendanceService;
import belstuattend.by.qr_attendance.services.DisciplineService;
import belstuattend.by.qr_attendance.services.UserService;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final UserService userService;
    private final DisciplineService disciplineService;
   

    @Autowired
    public AttendanceController(UserService userService, DisciplineService disciplineService,
                            AttendanceService attendanceService){
        this.userService = userService;
        this.disciplineService = disciplineService;
        this.attendanceService = attendanceService; 
    }
    
    //TODO переделать, пишу код написан в не трезвом состоянии 
    @PostMapping("/record")
    public ResponseEntity<?> recordAttendance(@RequestBody RecordDTO recordDTO){

        if(recordDTO.discipline() != null && recordDTO.code() != null){
            return ResponseEntity.badRequest().body("Отсутствует код или название дисциплины!");
        }

        // DisciplineDTO discipline = disciplineService.findByName(recordDTO.discipline());

        Boolean resultOfRecording = attendanceService.recordUser(recordDTO);

        return resultOfRecording ? ResponseEntity.ok().body(new AttendanceDTO("Attendance recorded successfully")) : 
            ResponseEntity.badRequest().body(new AttendanceDTO("Error recording attendance"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserAttendances(){
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userService.findByLogin(username);

        return ResponseEntity.ok().body(user.getAttendances());
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAttendances(){
        List<UserDTO>  users = userService.findAll();
        return ResponseEntity.ok().body(users);
    }

}
