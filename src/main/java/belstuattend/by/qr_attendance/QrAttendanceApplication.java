package belstuattend.by.qr_attendance;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QrAttendanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrAttendanceApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(){
		return args -> {
			// disciplineService.initDefaultDisciplines();
			// System.out.println("Инициализация дисциплин завершена");
		};
	}

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
}
