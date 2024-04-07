package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.services.Cip30Service;
import com.walruscode.cardano.services.EncryptionService;
import com.walruscode.cardano.services.WalletService;
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
    private WalletService walletService;
    private EncryptionService encryptionService;

    @BeforeEach
    public void setup() {
        cip30Service = Mockito.mock(Cip30Service.class);
        walletService = Mockito.mock(WalletService.class);
        encryptionService = Mockito.mock(EncryptionService.class);
    }

    @Test
    public void validateSignTest() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result(
                        "{\"nonce\": \"asdasdsd\", \"stakeAddress\": \"asadwqeqe1321\"}",
                        "sign")))
                .when(cip30Service).verify(anyString(), anyString());
        Mockito.doReturn(true).when(walletService).isValid(anyString(), anyString(), anyString());
        Mockito.doReturn("encryptedCookie")
                .when(encryptionService).encrypt(anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);
    }

    @Test
    public void validateSignTestEncryptionFails() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result(
                        "{\"nonce\": \"asdasdsd\", \"stakeAddress\": \"asadwqeqe1321\"}",
                        "sign")))
                .when(cip30Service).verify(anyString(), anyString());
        Mockito.doReturn(true).when(walletService).isValid(anyString(), anyString(), anyString());
        Mockito.doThrow(new RuntimeException("encryption failed"))
                .when(encryptionService).encrypt(anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 500);
    }

    @Test
    public void validateSignTestNotValidWallet() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result(
                "{\"nonce\": \"asdasdsd\", \"stakeAddress\": \"asadwqeqe1321\"}",
                        "sign")))
                .when(cip30Service).verify(anyString(), anyString());
        Mockito.doReturn(false).when(walletService).isValid(anyString(), anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestBadRequest() {
        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"sign\": \"asdasdsd\", \"key2\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestBadRequestv2() {
        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "notajsonstring";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void validateSignTestCip30Fails() throws Exception {
        Mockito.doThrow(new RuntimeException()).when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"sign\": \"asdasdsd\", \"key\": \"assadad\", \"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.validateSign(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);
    }

    @Test
    public void getNonceTest() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"stakeAddress\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.getAndSaveNonce(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 200);

        ArgumentCaptor<String> address = ArgumentCaptor.captor();

        Mockito.verify(walletService, Mockito.times(1))
                .saveWallet(address.capture(), anyString(), any());

        Assertions.assertEquals(address.getValue(), "asadwqeqe1321");
    }

    @Test
    public void getNonceTestInvalid() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "{\"stakeAddress2\": \"asadwqeqe1321\"}";

        Map<String, Object> response = app.getAndSaveNonce(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);

        Mockito.verify(walletService, Mockito.times(0))
                .saveWallet(anyString(), anyString(), any());
    }

    @Test
    public void getNonceTestInvalidBody() throws Exception {
        Mockito.doReturn(Optional.of(new Cip30Service.Cip30Result("key", "sign")))
                .when(cip30Service).verify(anyString(), anyString());

        final App app = new App(new Gson(), cip30Service, walletService, encryptionService);

        String body = "notajsonbody";

        Map<String, Object> response = app.getAndSaveNonce(Map.of("body", body));

        Assertions.assertEquals(response.get("statusCode"), 400);

        Mockito.verify(walletService, Mockito.times(0))
                .saveWallet(anyString(), anyString(), any());
    }
}
