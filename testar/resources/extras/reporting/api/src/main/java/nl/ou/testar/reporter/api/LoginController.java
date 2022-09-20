package nl.ou.testar.reporter.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import nl.ou.testar.reporter.model.AuthenticationToken;
import nl.ou.testar.reporter.model.Credentials;
import nl.ou.testar.reporter.security.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService service;

    @Operation(summary = "Post login credentials and obtain authorization token")
    @PostMapping
    public ResponseEntity<AuthenticationToken> loginToken(
            @RequestBody
            @Parameter(description = "JSON credentials containing username and password")
            Credentials credentials
    ) {
        AuthenticationToken token = service.loginToken(credentials);
        if (token == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
