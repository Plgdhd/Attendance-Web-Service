package belstuattend.by.qr_attendance.services;


import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer.Discoverers;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import belstuattend.by.qr_attendance.dto.DisciplineDTO;
import belstuattend.by.qr_attendance.exceptions.DisciplineAlreadyExistsException;
import belstuattend.by.qr_attendance.exceptions.DisciplineNotFoundException;
import belstuattend.by.qr_attendance.models.Discipline;
import belstuattend.by.qr_attendance.repository.DisciplineRepository;

@Service
public class DisciplineService {
    
    private final DisciplineRepository disciplineRepository;
    private final ModelMapper modelMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public DisciplineService(DisciplineRepository disciplineRepository, ModelMapper modelMapper,
                    RedisTemplate<String, String> redisTemplate){
        this.disciplineRepository = disciplineRepository;
        this.modelMapper = modelMapper;
        this.redisTemplate = redisTemplate;
    }

    public List<DisciplineDTO> findAll(){
        return disciplineRepository.findAll()
            .stream()
            .map(discipline -> modelMapper.map(discipline, DisciplineDTO.class))
            .toList();
    }

    public DisciplineDTO findByName(String name){
        Optional<Discipline> discipline = disciplineRepository.findByName(name);

        if(discipline.isEmpty()){
            throw new DisciplineNotFoundException("Ошибка при получении дисциплины по имени");
        }

        return modelMapper.map(discipline.get(), DisciplineDTO.class);
    }

    public DisciplineDTO save(DisciplineDTO disciplineDTO){

        Discipline discipline = modelMapper.map(disciplineDTO, Discipline.class);

        if(disciplineRepository.findByName(discipline.getName()).isPresent()){
            throw new DisciplineAlreadyExistsException("Такая дисциплина уже существует");
        }

        return modelMapper.map(disciplineRepository.save(discipline), DisciplineDTO.class);
    }

    public DisciplineDTO update(DisciplineDTO disciplineDTO){

        Optional<Discipline> discipline = disciplineRepository.findByName(disciplineDTO.name());

        if(discipline.isEmpty()){
            throw new DisciplineNotFoundException("Дисциплина с таким именем не найдена");
        }

        discipline.get().setName(disciplineDTO.name());
        discipline.get().setDescription(disciplineDTO.description());

        return modelMapper.map(disciplineRepository.save(discipline.get()), DisciplineDTO.class);

    }

    public void deleteByName(String name){
        Optional<Discipline> discipline = disciplineRepository.findByName(name);
    
        if(discipline.isEmpty()){
            throw new DisciplineAlreadyExistsException("Такой дисциплины не существует"); // мне лень писать еще одного исключение
        }

        disciplineRepository.delete(discipline.get());
    }

    //взято со старого кода этого сервиса!!!
    //проверка существования дисциплины (ЧО ЗА АЙДИ БЛЯТЬ)
    public boolean existsById(String id) {
        return disciplineRepository.existsById(id);
    }
    //проверка по имени тру фалсу если есть нет пон?
    public boolean existsByName(String name) {
        return disciplineRepository.findByName(name).isPresent();
    }

    //инициализация дисциплин по умолчанию (если нет ни одной)
    public void initDefaultDisciplines() {
        if (disciplineRepository.count() == 0) {
            List<Discipline> defaultDisciplines = List.of(
                new Discipline("Основы Алгоритмизации и программирования", "Базовый курс по алгоритмам и программированию"),
                new Discipline("Математический анализ", "Курс по дифференциальному и интегральному исчислению"),
                new Discipline("Базы данных", "Теория и практика работы с базами данных"),
                new Discipline("Операционные системы", "Изучение ОС и их архитектуры"),
                new Discipline("Компьютерные сети", "Основы сетевых технологий")
            );
            
            disciplineRepository.saveAll(defaultDisciplines);
        }
    }

    public String getCurrentCodeForDiscipline(String disciplineName){
        return redisTemplate.opsForValue().get(disciplineName);
    }
}
