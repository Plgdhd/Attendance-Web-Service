package belstuattend.by.qr_attendance.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import belstuattend.by.qr_attendance.models.Discipline;

@Repository
public interface DisciplineRepository extends MongoRepository<Discipline, String>{
    Optional<Discipline> findByName(String name);
}
