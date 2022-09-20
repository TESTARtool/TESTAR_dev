package nl.ou.testar.reporter.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static nl.ou.testar.reporter.security.SecurityConstants.*;

@Slf4j
public class AccessTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(TOKEN_PREFIX)) {
            log.info("Authorization header available");
            SecurityContextHolder.getContext().setAuthentication(getAuthentication(header));
        }
        else {
            log.info("Authorization header unavailable");
        }
        log.info("URI: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(String header) {
        String user = JWT.require(Algorithm.HMAC256(SECRET))
                .build()
                .verify(header.replace(TOKEN_PREFIX, ""))
                .getSubject();

        if (user == null) {
            return null;
        }
        return new UsernamePasswordAuthenticationToken(user, null,
                AuthorityUtils.createAuthorityList("ROLE_USER"));
    }
}
