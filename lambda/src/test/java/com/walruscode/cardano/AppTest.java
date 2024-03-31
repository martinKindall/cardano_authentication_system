package com.walruscode.cardano;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AppTest {

    @Test
    public void validateSignTest() {
        final App app = new App(new Gson());

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);
    }

    @Test
    public void validateSignTestBadRequest() {
        final App app = new App(new Gson());

        String body = "{\"sign\": \"asdasdsd\", \"key2\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestBadRequestv2() {
        final App app = new App(new Gson());

        String body = "notajsonstring";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }
}
