package com.example.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "oauth.apple")
public class AppleOauthProperties
{
    private String baseAuthUrl;
    private String clientId;
    private String redirectUri;
    private String responseType;
    private String scope;
    private String responseMode;
    private String state;
    private String tokenUrl;
    private String teamId;
    private String keyId;
    private String grantType;
}
