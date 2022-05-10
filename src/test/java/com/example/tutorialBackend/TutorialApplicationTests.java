package com.example.tutorialBackend;

import static org.springframework.web.util.UriComponentsBuilder.*;

import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.tutorialBackend.dto.response.AlarmResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

class TutorialApplicationTests {

    @Test
    void unitTest() {
        ObjectMapper objectMapper = new ObjectMapper();
        String result = "\"{\"opcode\":\"SEND_ALARM\",\"data\":{\"data\":\"[새 글 작성됨] 12hz\",\"isRead\":false}}\"";
        try {
            AlarmResponse r = objectMapper.readValue(result, AlarmResponse.class);
            System.out.println(r);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void contextLoads() {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String file ="";
            for(int i = 1; i <= 1761; i+=10) {
                file = i+"-"+(i+9);
                Path path = Paths.get("/Users/user/IdeaProject/webSocketTest/input/"+file+".json");
                String content = Files.readString(path);
                List<Map> m = objectMapper.readValue(content, List.class);
                for(Map map : m) {
                    String token = map.get("wcs_auth_token").toString();
                    String tokens[] = token.split("\\.");
                    int count = tokens.length;
                    if(count!=3 && count > 1) {
                        System.out.println(token);
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    void stopWatch() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            Thread.sleep(1000);
            stopWatch.stop();

            stopWatch.start();
            Thread.sleep(500);
            stopWatch.stop();


            stopWatch.start();
            Thread.sleep(1200);
            stopWatch.stop();


            System.out.println(stopWatch.prettyPrint());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


}
