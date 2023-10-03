package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleIdToken
{
    private String iss;
    private String aud;
    private Long exp;
    private Long iat;
    private String sub;// users unique id
    @JsonProperty("at_hash")
    private String atHash;
    @JsonProperty("auth_time")
    private Long authTime;
    @JsonProperty("nonce_supported")
    private Boolean nonceSupported;
    @JsonProperty("email_verified")
    private Boolean emailVerified;
    private String email;
}
