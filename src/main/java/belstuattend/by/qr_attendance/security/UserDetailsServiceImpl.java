package belstuattend.by.qr_attendance.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import belstuattend.by.qr_attendance.exceptions.UserNotFoundException;
import belstuattend.by.qr_attendance.models.User;
import belstuattend.by.qr_attendance.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByLogin(username);
        
        if(user.isEmpty()){
            throw new UserNotFoundException("Пользователь с именем " + username + " не найден");
        }

        return new UserDetailsImpl(user.get());
    }
    
}
