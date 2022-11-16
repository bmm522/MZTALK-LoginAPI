package com.loginAPI.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2LoginService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void getGoogleUserInfo(String credential) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+credential);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("code",credential);
//        body.add("client_id", "91389855205-g349trun5m9m0gjcvto3kqiob2gebsu9.apps.googleusercontent.com");
//        body.add("client_secret","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");
//        body.add("redirect_uri","http://localhost:3000");
//        body.add("grant_type","authorization_code");

        HttpEntity<MultiValueMap<String, String>> he = new HttpEntity<>(body,headers);

        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/drive/v2/files",
                HttpMethod.GET,
                he,
                String.class

        );

        System.out.println(response.getBody());
    }

}
