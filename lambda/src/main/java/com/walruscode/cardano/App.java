package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.dto.Cookie;
import com.walruscode.cardano.dto.Payload;
import com.walruscode.cardano.dto.SignPayload;
import com.walruscode.cardano.services.Cip30Service;
import com.walruscode.cardano.services.EncryptionService;
import com.walruscode.cardano.services.WalletService;

import java.time.Instant;
import java.util.*;

import static com.walruscode.cardano.Utils.Utils.generateRandomString;

public class App {

    private final Gson gson;
    private final Cip30Service cip30Service;
    private final WalletService walletService;
    private final EncryptionService encryptionService;

    public App(Gson gson, Cip30Service cip30Service, WalletService walletService,
               EncryptionService encryptionService) {
        this.gson = gson;
        this.cip30Service = cip30Service;
        this.walletService = walletService;
        this.encryptionService = encryptionService;
    }

    public Map<String, Object> getAndSaveNonce(Map<String, Object> request) {
        Optional<Payload> payload = getPayloadParams(request);

        if (payload.isEmpty()) {
            return Map.of("statusCode",400);
        }

        String nonce = generateRandomString();

        walletService.saveWallet(payload.get().stakeAddress(), nonce, Instant.now());

        return Map.of("statusCode",200, "body", Map.of("message", new Cookie(
                nonce, payload.get().stakeAddress()
        )));
    }

    public Map<String, Object> validateSign(Map<String, Object> request) {
        Optional<SignPayload> signPayload = getSignPayloadParams(request);

        if (signPayload.isEmpty()) return Map.of("statusCode",400);

        Optional<Cip30Service.Cip30Result> result;

        try {
            result = cip30Service.verify(signPayload.get().key(), signPayload.get().sign());
        } catch (Exception e) {
            result = Optional.empty();
        }

        if (result.isEmpty()) return Map.of("statusCode",400);

        Cookie message = gson.fromJson(result.get().message(), Cookie.class);

        // verify address and nonce with database
        boolean isValid = walletService.isValid(result.get().stakeAddress(), signPayload.get().stakeAddress(),
                message.nonce());

        if (!isValid) return Map.of("statusCode",400);

        try {
            String cookie = encryptionService.encrypt(gson.toJson(message));

            return Map.of("statusCode",200, "body","Signature validated, redirect to showContent",
                    "headers", Map.of("Set-Cookie", cookie));

        } catch (Exception e) {
            return Map.of("statusCode",500, "body", "Error code 5001");
        }
    }

    public Map<String, Object> showContent(Map<String, Object> request) {
        PayloadCookie payloadCookie = getDecryptedCookie(request);

        if (payloadCookie.payload().isEmpty()) {
            return Map.of("statusCode",400);
        }

        if (payloadCookie.cookie().isEmpty()) {
            return Map.of("statusCode",200, "body","Redirect to login and clear cookie");
        }

        return Map.of("statusCode",200,"body","Foo bar");
    }

    private PayloadCookie getDecryptedCookie(Map<String, Object> request) {
        Optional<Payload> payload = getPayloadParams(request);

        if (payload.isEmpty()) return new PayloadCookie(Optional.empty(), Optional.empty());

        Optional<String> cookie = getCookie(request);

        if (cookie.isEmpty()) return new PayloadCookie(payload, Optional.empty());

        return new PayloadCookie(payload, validateCookie(cookie.get(), payload.get().stakeAddress()));
    }

    private Optional<String> validateCookie(String encryptedCookie, String payloadStakeAddress) {

        try {
            String decryptedCookie = encryptionService.decrypt(encryptedCookie);
            Cookie cookie = gson.fromJson(decryptedCookie, Cookie.class);

            return Objects.equals(cookie.stakeAddress(), payloadStakeAddress) ? Optional.of(decryptedCookie) :
                    Optional.empty();

        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Optional<Payload> getPayloadParams(Map<String, Object> request) {
        String body = (String) request.get("body");

        if (body == null) return Optional.empty();

        try {
            return Optional.of(gson.fromJson(body, Payload.class));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private Optional<SignPayload> getSignPayloadParams(Map<String, Object> request) {
        String body = (String) request.get("body");

        if (body == null) return Optional.empty();

        try {
            return Optional.of(gson.fromJson(body, SignPayload.class));
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private Optional<String> getCookie(Map<String, Object> request) {
        List<String> cookies = (List<String>) request.get("cookies");

        if (cookies == null || cookies.isEmpty()) return Optional.empty();

        return Optional.of(cookies.getFirst());
    }

    private record PayloadCookie(Optional<Payload> payload, Optional<String> cookie) {}
}
