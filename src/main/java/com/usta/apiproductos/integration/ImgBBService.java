package com.usta.apiproductos.integration;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Map;

@Service
public class ImgBBService {
    private final String API_KEY = "29bfea99aa30ac4112ec43eac8554d9c";

    public String subirImagen(String base64) {

        RestTemplate rest = new RestTemplate();

        String url =
                "https://api.imgbb.com/1/upload?key=" + API_KEY;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body);

        Map response = rest.postForObject(url, request, Map.class);

        Map data = (Map) response.get("data");

        return data.get("url").toString();
    }
}
