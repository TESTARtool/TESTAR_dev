package nl.ou.testar.reporter.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import nl.ou.testar.reporter.model.AuthenticationToken;
import nl.ou.testar.reporter.model.Credentials;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

import static nl.ou.testar.reporter.security.SecurityConstants.EXPIRATION_TIME;
import static nl.ou.testar.reporter.security.SecurityConstants.SECRET;

//@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;

    public AuthenticationToken loginToken(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            Date now = new Date();
            String tokenString = JWT.create()
                    .withSubject(username)
                    .withIssuedAt(now)
                    .withExpiresAt(new Date(now.getTime() + EXPIRATION_TIME))
                    .sign(Algorithm.HMAC256(SECRET));
            return new AuthenticationToken(tokenString);
        }
        catch (Exception e) {
            return null;
        }
    }
}
