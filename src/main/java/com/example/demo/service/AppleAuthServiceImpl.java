package com.example.demo.service;

import com.example.demo.config.AppleOauthProperties;
import com.example.demo.model.AppleIdToken;
import com.example.demo.model.OauthTokenResponse;
import com.example.demo.model.TokenResponse;
import com.example.demo.utils.DemoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(value = AppleOauthProperties.class)
public class AppleAuthServiceImpl implements AppleAuthService{

    private final AppleOauthProperties appleOauthProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public OauthTokenResponse createOauth2Token(){
        Map<String, String> credentials = new LinkedHashMap<>();
        credentials.put("client_id", appleOauthProperties.getClientId());
        credentials.put("redirect_uri", appleOauthProperties.getRedirectUri());
        credentials.put("response_type", appleOauthProperties.getResponseType());
        credentials.put("scope", appleOauthProperties.getScope());
        credentials.put("response_mode", appleOauthProperties.getResponseMode());
        credentials.put("state", appleOauthProperties.getState());
        String baseUrl = appleOauthProperties.getBaseAuthUrl();
        String url = DemoUtils.parseAuthUrlWithParams(baseUrl, credentials);
        return OauthTokenResponse.builder()
                .code("90009")
                .status("true")
                .authorizationUrl(url)
                .build();
    }

    @Override
    public AppleIdToken authorizeCode(String code) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", appleOauthProperties.getClientId());
        map.add("client_secret", generateJWT());
        map.add("grant_type", appleOauthProperties.getGrantType());
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<TokenResponse> responseEntity;
        TokenResponse tokenResponse = null;

        try{
            String url = appleOauthProperties.getTokenUrl();
             responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, TokenResponse.class);
             if(responseEntity.getStatusCode().is2xxSuccessful()){
                 log.info("Successfully exchanged authorization code for access token");
                 tokenResponse = responseEntity.getBody();
                 log.info("TOKEN RESPONSE: {}", objectMapper.writeValueAsString(tokenResponse));
             }else{
                 log.info("RESPONSE ENTITY: {}", objectMapper.writeValueAsString(responseEntity));
             }
        }catch (HttpServerErrorException | HttpClientErrorException exception){
            exception.printStackTrace();
            log.info("EXCEPTION RESPONSE BODY: {}", objectMapper.writeValueAsString(exception.getResponseBodyAsString()));
            log.error("Exception occurred while trying to connect to Apple's authorization code validation server...");
        }

        if(Optional.ofNullable(tokenResponse).isEmpty()){
            log.info("APPLE TOKEN RESPONSE IS NULL...");
            return new AppleIdToken();
        }

        String idToken = tokenResponse.getIdToken();
        log.info("ID TOKEN: {}", idToken);
        String payload = idToken.split("\\.")[1];// 0 is header we ignore it for now
        String decoded = new String(Decoders.BASE64.decode(payload));
        log.info("DECODED PAYLOAD: {}", decoded);
        return objectMapper.readValue(decoded, AppleIdToken.class);
    }

    private String generateJWT() throws Exception {
        PrivateKey pKey = generatePrivateKey();
        return Jwts.builder()
                .setHeaderParam(JwsHeader.KEY_ID, appleOauthProperties.getKeyId())
                .setIssuer(appleOauthProperties.getTeamId())
                .setAudience("https://appleid.apple.com")
                .setSubject("Interswitch")
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 5)))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(pKey, SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey generatePrivateKey() throws Exception {
        File file = new ClassPathResource("apple/cert.p8").getFile();
        final PEMParser pemParser = new PEMParser(new FileReader(file));
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        final PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        final PrivateKey pKey = converter.getPrivateKey(object);
        pemParser.close();
        return pKey;
    }

}
