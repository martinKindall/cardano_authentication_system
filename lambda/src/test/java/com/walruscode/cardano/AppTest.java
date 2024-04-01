package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.repositories.WalletRepository;
import com.walruscode.cardano.services.Cip30Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class AppTest {

    private Cip30Service cip30Service;
    private WalletRepository walletRepository;

    @BeforeEach
    public void setup() {
        cip30Service = Mockito.mock(Cip30Service.class);
        walletRepository = Mockito.mock(WalletRepository.class);
    }

    @Test
    public void validateSignTest() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletRepository);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);
    }

    @Test
    public void validateSignTestBadRequest() {
        final App app = new App(new Gson(), cip30Service, walletRepository);

        String body = "{\"sign\": \"asdasdsd\", \"key2\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestBadRequestv2() {
        final App app = new App(new Gson(), cip30Service, walletRepository);

        String body = "notajsonstring";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestCip30Fails() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletRepository);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void getNonceTest() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletRepository);

        String body = "{\"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.getAndSaveNonce(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);

        ArgumentCaptor<String> address = ArgumentCaptor.captor();

        Mockito.verify(walletRepository, Mockito.times(1))
                .saveWallet(address.capture(), anyString(), any());

        Assertions.assertEquals(address.getValue(), "asadwqeqe1321");
    }
}
