package com.example.demo.controller;

import com.example.demo.model.OauthTokenResponse;
import com.example.demo.service.AppleAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppleAuthController 
{
    private final AppleAuthService appleAuthService;

    @GetMapping(value = "/oauth/apple-sign-in/credentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OauthTokenResponse> getOauth2AppleAuthUrl(){
        return ResponseEntity.ok(appleAuthService.createOauth2Token());
    }

    // Redirect URI for test
    @GetMapping(value = "/oauth/apple-sign-in", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> authorizeCodeGet(@RequestParam("code") String code){
        log.info("-------------------- Starting Auth Code Validation GET -------------------");
        try{
            return ResponseEntity.ok(appleAuthService.authorizeCode(code));
        }catch (Exception exception){
            return ResponseEntity.ok("Acknowledge code but error retrieving access token");
        }
    }

    // Redirect URI for test
    @PostMapping(value = "/oauth/apple-sign-in", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> authorizeCodePost(@RequestParam("code") String code){
        log.info("------------------- Starting Auth Code Validation POST -------------------");
        try{
            return ResponseEntity.ok(appleAuthService.authorizeCode(code));
        }catch (Exception exception){
            return ResponseEntity.ok("Acknowledge code but error retrieving access token");
        }
    }
}
