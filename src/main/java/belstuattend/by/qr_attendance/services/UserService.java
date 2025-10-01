package belstuattend.by.qr_attendance.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.hibernate5.SpringSessionContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import belstuattend.by.qr_attendance.dto.UserDTO;
import belstuattend.by.qr_attendance.exceptions.UserNotFoundException;
import belstuattend.by.qr_attendance.models.User;
import belstuattend.by.qr_attendance.repository.UserRepository;

@Service
public class UserService {
    
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                        RedisTemplate<String, String> redisTemplate){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.redisTemplate = redisTemplate;
    }

    public UserDTO getCurrentUser(){
        //TODO добавить исключения
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userRepository.findByLogin(userDetails.getUsername()).get();
        UserDTO userDTO = modelMapper.map(currentUser, UserDTO.class);
        return userDTO;
    }

    public UserDTO save(User user){
        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    public UserDTO registerUser(User user){
        if(userRepository.findByLogin(user.getLogin()).isPresent()){
            throw new UserNotFoundException("Пользователь с таким логином уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCourse(2);
        user.setRole("ROLE_USER");

        return modelMapper.map(userRepository.save(user), UserDTO.class);
    }

    public User findByLogin(String login){
        Optional<User> user = userRepository.findByLogin(login);

        if(user.isEmpty()){
            throw new UserNotFoundException("Пользователь с таким логином не найден");
        }

        return user.get();
    } 

    public List<UserDTO> findAll(){
        List<UserDTO> users = userRepository.findAll()
            .stream()
            .map(user -> modelMapper.map(user, UserDTO.class))
            .toList();

        return users;
    }

    public UserDTO authentificateUser(String login, String password){

        Optional<User> user = userRepository.findByLogin(login);
        if(user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())){
            return modelMapper.map(user.get(), UserDTO.class);
        }
        else{
            throw new UserNotFoundException("Пользователь для валидации не найден");
        }
    }

    public void changePassword(String login, String newPassword){

        Optional<User> user = userRepository.findByLogin(login);
        if(user.isEmpty()){
            throw new UserNotFoundException("Пользователь для смены пароля не найден");
        }
        
        user.get().setPassword(newPassword);
        userRepository.save(user.get());

    }
    
    //Возможно стоит заменить на name
    public boolean canAttend(String id){
        String key = "attend_action:" + id;

        boolean isExists = redisTemplate.hasKey(key);
        if(isExists){
            return false;
        }

        redisTemplate.opsForValue().set(key, "attended", 180, TimeUnit.MINUTES);
        return true;
    }

    public boolean canAttendByName(String name){
        Optional<User> user = userRepository.findByLogin(name);
        if(user.isEmpty()){
            throw new UserNotFoundException("Пользователь с таким именем для отметки не найден");
        }
        return canAttend(user.get().getId());
    }

}
