package belstuattend.by.qr_attendance.controllers;

import org.apache.catalina.connector.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.JWTVerificationException;

import belstuattend.by.qr_attendance.dto.AuthentificationDTO;
import belstuattend.by.qr_attendance.dto.RegistrationDTO;
import belstuattend.by.qr_attendance.dto.UserDTO;
import belstuattend.by.qr_attendance.exceptions.UserNotFoundException;
import belstuattend.by.qr_attendance.models.User;
import belstuattend.by.qr_attendance.security.JWTUtil;
import belstuattend.by.qr_attendance.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(JWTUtil jwtUtil, ModelMapper modelMapper, UserService userService,
            AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthentificationDTO dto) {
        try {
            log.info("Register request: {}", dto);

            User user = modelMapper.map(dto, User.class);
            log.info("Mapped user: {}", user);

            userService.registerUser(user);
            String token = jwtUtil.generateToken(user.getLogin());
            return ResponseEntity.ok(new RegistrationDTO(token));
        } catch (Exception e) {
            log.error("Registration failed", e);
            return ResponseEntity.status(500).body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> performLogin(@RequestBody AuthentificationDTO authentificationDTO) {
        UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                authentificationDTO.getLogin(),
                authentificationDTO.getPassword());

        try {
            authenticationManager.authenticate(authInputToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }

        String token = jwtUtil.generateToken(authentificationDTO.getLogin());
        RegistrationDTO registrationDTO = new RegistrationDTO(token);
        return ResponseEntity.ok().body(registrationDTO);
    }

    @GetMapping("/getCurrentSession")
    public ResponseEntity<?> getCurrentUserSession() {
        return ResponseEntity.ok().body(userService.getCurrentUser());
    }
}
