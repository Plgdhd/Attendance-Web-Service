package belstuattend.by.qr_attendance.models;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "disciplines")
public class Discipline {
    
    @Id
    private String id;

    private String name;
    private String description;

    public Discipline(){}

    public Discipline(String name){
        this.name = name;
    }

    public Discipline(String name, String description){
        this.name = name;
        this.description = description;
    }
}
