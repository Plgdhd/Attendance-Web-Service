package belstuattend.by.qr_attendance.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import belstuattend.by.qr_attendance.dto.QrGenerationDTO;
import belstuattend.by.qr_attendance.dto.RecordDTO;
import belstuattend.by.qr_attendance.exceptions.QrCodeGenerationException;
import belstuattend.by.qr_attendance.services.DisciplineService;
import belstuattend.by.qr_attendance.services.QrGeneratorService;

@RestController
@RequestMapping("/api/qr")
public class QrController {
    
    private final DisciplineService disciplineService;
    private final QrGeneratorService qrGeneratorService;
    
    @Autowired
    public QrController(DisciplineService disciplineService, QrGeneratorService qrGeneratorService){
        this.disciplineService = disciplineService;
        this.qrGeneratorService = qrGeneratorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> qrGeneration(@RequestBody QrGenerationDTO qrGenerationDTO){
        try{
            if(!disciplineService.existsByName(qrGenerationDTO.discipline())){
                return ResponseEntity.badRequest().body("Дисциплина с таким именем не найдена");
            }

            BufferedImage icon = qrGeneratorService.generateUniqueQrCode(qrGenerationDTO.discipline());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(icon, "png", byteArrayOutputStream);
            byte[] byteIcon = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(byteIcon);
        }
        catch (IOException e) {
            return ResponseEntity.status(500).body("Ошибка при генерации QR-кода: " + e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyQrCode(@RequestBody RecordDTO recordDTO){

        String currentCode = disciplineService.getCurrentCodeForDiscipline(recordDTO.discipline());
        if(!recordDTO.code().equals(currentCode)){
            return ResponseEntity.badRequest().body("Код недействителен");
        }
        return ResponseEntity.ok().body("Текущий код для " + recordDTO.discipline() + " : " + recordDTO.code());
    }
}
