package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestTemplateService {

    // Spring boot에서 만들어진 것을 주입받아서 사용할 것
    private final RestTemplate restTemplate;

    public RestTemplateService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build(); // build()는 restTemplate를 반환함
    }

    public ItemDto getCallObject(String query) {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder // URI라는 클래스 객체를 만들 수 있음 (=우리가 요청할 URL을 만든다)
                .fromUriString("http://localhost:7070") // 서버 입장의 서버에다 보낼 준비를 하는 것
                .path("/api/server/get-call-obj")       
                .queryParam("query", query)       // requestparam 방식으로 되어 있으므로 ? 뒤에 값을 받아옴 (검색어를 보냄) (Controller에서 받아옴)
                .encode().build().toUri();

        log.info("uri = " + uri);

        // 요청을 보냈을 때, 데이터를 받음 (지금 Item을 물어봄. Item에 대한 정보를 ItemDto로 받을 것임)
        // getForEntity는 get 방식으로 해당 URI 서버에 요청을 보냄
        // 두 번째 parameter로 우리가 그 서버 입장의 서버, 요청을 한 그 서버에서 넘어오는 데이터를 받아줄 클래스 타입을 지정하면 자동으로 역직렬화가 되어서 객체 형태로 담김
        // 받는 것은 ResponseEntity로 받을 수 있음 (ItemDto 타입으로)
        ResponseEntity<ItemDto> responseEntity = restTemplate.getForEntity(uri, ItemDto.class);

        // statuscode 서버 쪽에서도 날아옴 (성공하면 200이 날라올 것)
        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    public List<ItemDto> getCallList() {
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070") //http://locaohost:7070
                .path("/api/server/get-call-list")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        // 여기서는 그냥 String으로 받음
        // 위에서는 Json이 하나였는데, 여기서는 복합적이기 때문에 그냥 String으로 받은 다음
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);

        log.info("statusCode = " + responseEntity.getStatusCode());
        log.info("Body = " + responseEntity.getBody());

        // 여기서 다시 반환함 (json으로)
        return fromJSONtoItems(responseEntity.getBody());
    }

    public ItemDto postCall(String query) {
        // 요청 URI 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/post-call/{query}") // 데이터를 받거나 넣고 싶은 부분에 중괄호로 표시 (데이터가 동적으로 바뀔 path부분을 중괄호로 표시)
                .encode()
                .build()
                .expand(query) // 데이터를 동적으로 넣어주는 방법 - 여기에 들어갈 값을 query에 맞게 넘어오면 path의 query쪽에 값으로 들어감
                .toUri();
        log.info("uri = "+uri);

        // 객체 하나 생성
        User user = new User("Robbie", "1234");

        // Post for Entity (body 부분에 데이터를 넘길 수 있음)
        // 첫 번째 파라미터로는 uri, 두번째 파라미터로는 HttpBody에 넣어줄 데이터를 넣으면 됨(우리는 user 객체 데이터)
        // 세 번째는 전달받은 데이터와 매핑할 값
        ResponseEntity<ItemDto> responseEntity = restTemplate.postForEntity(uri, user, ItemDto.class);

        log.info("statusCode = " + responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    public List<ItemDto> exchangeCall(String token) {
        // 요청 URI 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:7070")
                .path("/api/server/exchange-call")
                .encode()
                .build()
                .toUri();
        log.info("uri = " + uri);

        User user = new User("Robbie", "1234");

        RequestEntity<User> requestEntity = RequestEntity
                .post(uri) // post 방식 지정
                .header("X-Authorization", token)
                .body(user); // body에 user를 넣어줄 것

        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

        return fromJSONtoItems(responseEntity.getBody());
    }

/* {
"items": {
    {"title":"Mac", "price":3888000},
    {"title":"iPad", "price":1230000},
    {"title":"iPhone", "price":1550000},
    {"title":"Watch", "price":450000},
    {"title":"AirPods", "price":350000}
  }
} */

    public List<ItemDto> fromJSONtoItems(String responseEntity) {
        // 중첩 JSON의 형태로 넘어옴 (이 데이터를 우선 String으로 받아오게 됨)
        JSONObject jsonObject = new JSONObject(responseEntity);
        // getJSONArray - key값으로 items를 줌 (그 값이 items 임 !)
        // 그러면 items의 내부의 값들만 저장되게 됨
        JSONArray items = jsonObject.getJSONArray("items");
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Object item: items) {
            // items 내부의 값들 하나씩 (위의 items 안에서 중괄호 한 줄 씩)
            ItemDto itemDto = new ItemDto((JSONObject) item);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }
}

/* 우리가 Object로 받아 올 때는 그냥 변환할 class 타입을 그냥 줬었는데,
* 배열 형식으로 넘어올 경우 즉, 중첩 JSON 형식으로 넘어올 때는 그냥 String.class 타입으로 그 데이터를 한 번에 받음
* 그러면 String으로 쭉 넘어올 것임.
* 그러면 Json을 도와주는 라이브러리를 사용해서 JSONObject JSONArray를 사용해서 String으로 되어있는 중첩 JSON을 조작함*/
