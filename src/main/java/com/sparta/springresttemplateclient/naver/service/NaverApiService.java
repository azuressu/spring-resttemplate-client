package com.sparta.springresttemplateclient.naver.service;

import com.sparta.springresttemplateclient.naver.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j(topic = "NAVER API")
@Service
public class NaverApiService {

    private final RestTemplate restTemplate;

    public NaverApiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<ItemDto> searchItems(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/shop.json")
                .queryParam("display", 15) // default가 10인데, 15개씩 가져오도록 속성값을 설정
                .queryParam("query", query)
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)   // get 방식으로 요청하라고 함
                .header("X-Naver-Client-Id", "ZxUMLC1B2OHLo0Ys9p2E")
                .header("X-Naver-Client-Secret", "Pzr5jypkOB")
                .build();

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        log.info("NAVER API Status Code : " + responseEntity.getStatusCode());

        return fromJSONtoItems(responseEntity.getBody());
    }

    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        JSONObject jsonObject = new JSONObject(responseEntity);
        JSONArray items = jsonObject.getJSONArray("items");
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Object item: items) {
            ItemDto itemDto = new ItemDto((JSONObject) item);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }


}