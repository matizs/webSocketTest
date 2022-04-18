package com.example.tutorialBackend;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.example.tutorialBackend.utils.NonClosingRestTemplate;

@SpringBootApplication
public class TutorialApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutorialApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    @Bean(name = "nonClosingRestTemplate")
    public RestTemplate nonClosingRestTemplate(){
        RestTemplate nonClosingRestTemplate = new NonClosingRestTemplate(Collections.singletonList(new ResourceHttpMessageConverter(true)));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        restTemplate().setRequestFactory(requestFactory);

        return nonClosingRestTemplate;
    }
}
