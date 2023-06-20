package com.sparta.springresttemplateclient.controller;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.service.RestTemplateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class RestTemplateController {


    private final RestTemplateService restTemplateService;

    public RestTemplateController(RestTemplateService restTemplateService) {
        this.restTemplateService = restTemplateService;
    }

    // api/client/get-call-obj를 get 방식으로 호출하면
    // 클라이언트 입장 서버의 Controller로 옴
    // 그리고 queryString으로 받아온 것을 service로 보냄
    // Service로 넘어와서 URL을 만듦 (방향이 서버 입장의 서버임)
    // 그리고 API path를 줌 -> /api/server/get-call/obj & 그리고 queryString을 붙임
    // 그 다음 restTemplate을 사용해서 호출함
    // 호출을 하면 server입장의 서버로 넘어감
    // get-call-obj로 가서 service로 넘어가서 해당 메소드를 실행한 다음 결과값을 다시
    // client 서버의 service로 돌아옴
    // 그래서 그 값이 ResponseEntity로 들어오게 됨 (ItemDto 타입으로 받음)
    // 마무리로 getBody를 하면, responseEntity 안에 들어있는 ItemDto 값을 반환할 수 있음

    @GetMapping("/get-call-obj")
    public ItemDto getCallObject(String query) { // @RequestParam 어노테이션 생략 가능
        return restTemplateService.getCallObject(query);
    }

    @GetMapping("/get-call-list")
    public List<ItemDto> getCallList() {
        return restTemplateService.getCallList();
    }

    @GetMapping("/post-call")
    public ItemDto postCall(String query) {
        return restTemplateService.postCall(query);
    }

    // @RequestHeader 어노테이션을 사용해서 가져올 key값(Authorization)을 넣어주고 뒤에는 원하는 String 명으로 받아옴
    @GetMapping("/exchange-call")
    public List<ItemDto> exchangeCall(@RequestHeader("Authorization") String token) {
        return restTemplateService.exchangeCall(token);
    }

}
