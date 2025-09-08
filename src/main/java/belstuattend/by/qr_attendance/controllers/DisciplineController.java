package belstuattend.by.qr_attendance.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import belstuattend.by.qr_attendance.dto.DisciplineDTO;
import belstuattend.by.qr_attendance.exceptions.DisciplineAlreadyExistsException;
import belstuattend.by.qr_attendance.exceptions.DisciplineNotFoundException;
import belstuattend.by.qr_attendance.services.DisciplineService;

@RestController
@RequestMapping("/api/disciplines")
public class DisciplineController {
    
    private final DisciplineService disciplineService;

    @Autowired
    public DisciplineController(DisciplineService disciplineService){
        this.disciplineService = disciplineService;
    }

    @GetMapping
    public ResponseEntity<List<DisciplineDTO>> getAllDisciplines(){
        return ResponseEntity.ok().body(disciplineService.findAll());
    }

    //TODO взято из прошлого проекта, нужно обдумать что переделать
    @GetMapping("/{id}")
    public ResponseEntity<DisciplineDTO> getDisciplineById(@PathVariable String id) {
        String discipline = "";
        switch(Integer.parseInt(id)){
            case 1:
                discipline = "Основы Алгоритмизации и программирования"; 
                break;
            default: 
                discipline = "Основы Алгоритмизации и программирования"; 
                break;
        }
        return ResponseEntity.ok().body(disciplineService.findByName(discipline));
    }

    @PostMapping("/add")
    public ResponseEntity<?> createDiscipline(@RequestBody DisciplineDTO disciplineDTO){
        
        if(disciplineDTO.name().isEmpty() || disciplineDTO.name() == null){
            return ResponseEntity.badRequest().body("Имя дисциплины отсутствует или не может быть пустым");
        }

        if(disciplineService.findByName(disciplineDTO.name()) != null){
            return ResponseEntity.badRequest().body("Дисциплина с таким именем не найдена");
        }

        return ResponseEntity.ok().body(disciplineService.save(disciplineDTO));
    }  

    //также переделать возможно
    @PostMapping("/update")
    public ResponseEntity<?> updateDiscipline(@RequestBody DisciplineDTO disciplineDTO){
        
        if(disciplineDTO.name() == null || disciplineDTO.name().isEmpty()){
            return ResponseEntity.badRequest().body("Название дисциплины не указано или не может быть пустым");
        }

        return ResponseEntity.ok().body(disciplineService.update(disciplineDTO));

    }
    //взято из прошлого проекта
    @PostMapping("init-default")
    public ResponseEntity<String> initDefaultDiscilpines(){
        disciplineService.initDefaultDisciplines();
        return ResponseEntity.ok().body("Дисциплины по умолчанию созданы успешно");
    }

}
