package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.services.Cip30Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;

public class AppTest {

    private Cip30Service cip30Service;

    @BeforeEach
    public void setup() {
        cip30Service = Mockito.mock(Cip30Service.class);
    }

    @Test
    public void validateSignTest() {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);
    }

    @Test
    public void validateSignTestBadRequest() {
        final App app = new App(new Gson(), cip30Service);

        String body = "{\"sign\": \"asdasdsd\", \"key2\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestBadRequestv2() {
        final App app = new App(new Gson(), cip30Service);

        String body = "notajsonstring";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }
}
