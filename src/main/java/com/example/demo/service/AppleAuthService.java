package com.example.demo.service;

import com.example.demo.model.AppleIdToken;
import com.example.demo.model.OauthTokenResponse;

public interface AppleAuthService
{

    OauthTokenResponse createOauth2Token();

    AppleIdToken authorizeCode(String code) throws Exception;
}
