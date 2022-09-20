package nl.ou.testar.reporter.security;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Slf4j
//@Configuration
public class WebSecurityConfig {// extends WebSecurityConfigurerAdapter {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        http = http.cors().and().csrf().disable();
//
//        http = http
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and();
//
//        http = http
//                .exceptionHandling()
//                .authenticationEntryPoint(
//                        (request, response, ex) -> {
//                            response.sendError(
//                                    HttpServletResponse.SC_UNAUTHORIZED,
//                                    ex.getMessage()
//                            );
//                            log.error("Authentication failed", ex);
//                        }
//                )
//                .and();
//
//        http.authorizeRequests()
//                // Our public endpoints
////			.anyRequest().permitAll();
//                .antMatchers("/login", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                .anyRequest().authenticated();
//
//        http.addFilterBefore(new AccessTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//    }
//
////	@Bean
////	public WebSecurityCustomizer webSecurityCustomizer() {
////		return web -> web.ignoring().antMatchers("/login");
////	}
//
//    @Override
//    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .ldapAuthentication()
//                .userDnPatterns("cn={0},ou=users")
////                .groupSearchBase("ou=groups")
//                .contextSource()
//                .url("ldap://ldap:1389/dc=testar,dc=ou,dc=nl")
//                .and()
//                .passwordCompare()
//                .passwordEncoder(new BCryptPasswordEncoder())
//                .passwordAttribute("userPassword");
//    }
//
//    @Bean
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }
}
