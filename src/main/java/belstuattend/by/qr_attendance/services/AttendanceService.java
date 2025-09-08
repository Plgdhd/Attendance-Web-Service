package belstuattend.by.qr_attendance.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import belstuattend.by.qr_attendance.dto.RecordDTO;
import belstuattend.by.qr_attendance.exceptions.RecordException;
import belstuattend.by.qr_attendance.exceptions.WrongCodeException;
import belstuattend.by.qr_attendance.models.User;

@Service
public class AttendanceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final DisciplineService disciplineService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public AttendanceService(RedisTemplate<String, String> redisTemplate, DisciplineService disciplineService,
            UserService userService, ModelMapper modelMapper) {
        this.redisTemplate = redisTemplate;
        this.disciplineService = disciplineService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    public Boolean recordUser(RecordDTO recordDTO) {

        String currentCode = disciplineService.getCurrentCodeForDiscipline(recordDTO.discipline());
        if (!recordDTO.code().equals(currentCode)) {
            throw new WrongCodeException("Код недействителен");
        }

    try{
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByLogin(userDetails.getUsername());

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy.HH.mm");
        String dateTime = now.format(formatter);

        Map<String, List<String>> userAttendances = user.getAttendances();
        if (userAttendances == null) {
            userAttendances = new HashMap<>();
            user.setAttendances(userAttendances);
        }

        if (!userService.canAttendByName(user.getLogin())) {
            return false;
        }

        List<String> disciplineAttendances = userAttendances
                .computeIfAbsent(recordDTO.discipline(), k -> new ArrayList<>());
        disciplineAttendances.add(dateTime);

        userService.save(user);
        return true;
    }
    catch(RecordException e){
        throw new RecordException(e.getMessage());
    }
}

}
