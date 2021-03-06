package com.kma.libraby.web.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kma.libraby.security.jwt.JWTFilter;
import com.kma.libraby.security.jwt.TokenProvider;
import com.kma.libraby.service.dto.LoginRequest;
import com.kma.libraby.web.errors.ErrorConstants;
import com.kma.libraby.web.errors.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class UserJWTController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider tokenProvider;


    @PostMapping("/auth")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginRequest login){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login.getUserId(),login.getPassword())
            );
            final String jwt = tokenProvider.createToken(authentication);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER,"Bearer "+jwt);
            return new ResponseEntity<>(new JWTToken(jwt),httpHeaders, HttpStatus.OK);
        }catch (Exception ex){
            throw new BadCredentialsException(ErrorConstants.BAD_CREDENTIALS);
        }
    }

    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

}