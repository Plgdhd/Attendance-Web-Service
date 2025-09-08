package belstuattend.by.qr_attendance.security;

import java.time.ZonedDateTime;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JWTUtil {
    
    @Value("${JWT_SECRET}")
    private String secret;

    //взято из прошлого кода
    @Value("${JWT_EXPIRATION:3}") 
    private long validityWeeks;

    public String generateToken(String username){

        Date expirationDate = Date.from(ZonedDateTime.now().plusWeeks(validityWeeks).toInstant());

        return JWT.create()
                .withSubject("User details")
                .withClaim("username", username)
                .withIssuedAt(new Date())
                .withIssuer("Attendance service")
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secret)); // TODO добавить позже в passwordEncoder

        
    }

    public String validateTokenAndRetrieveClaim(String token){
        try{

            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret))
                .withSubject("User details")
                .withIssuer("Attendance service")
                .build();
            
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("username").asString();
        }
        catch(JWTVerificationException e){
            throw new JWTVerificationException("Ошибка при проверке токена: " + e);  //TODO добавить в контроллер
        }
    }
}
