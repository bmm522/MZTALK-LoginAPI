package com.loginAPI.oauth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.boot.web.servlet.server.Jsp;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class NaverOAuth2LoginService {


    public void getNaverUserInfo(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+accessToken);

        HttpEntity<MultiValueMap<String, String>> he = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                he,
                String.class

        );

        System.out.println(response.getBody());

        JsonParser jsonParser = new JsonParser();
        JsonObject naverUserInfoJsonObject = (JsonObject) jsonParser.parse(response.getBody());
        String id = naverUserInfoJsonObject.get("response").getAsJsonObject().get("id").getAsString();
        String email = naverUserInfoJsonObject.get("response").getAsJsonObject().get("email").getAsString();


        System.out.println(id);
        System.out.println(email);


    }
}
