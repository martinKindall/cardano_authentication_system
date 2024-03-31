package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.dto.Payload;
import com.walruscode.cardano.dto.SignPayload;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {

    private final Gson gson;

    public App(Gson gson) {
        this.gson = gson;
    }

    public Map<String, Object> getAndSaveNonce(Map<String, Object> request) {
        Optional<Payload> payload = getPayloadParams(request);

        if (payload.isEmpty()) {
            return Map.of("statusCode",400);
        }

        String nonce = "somerandomnonce";

        saveNonceAndAddress(payload.get().stakeAddress(), nonce);

        return Map.of("statusCode",200, "body","Needs SignData: " + nonce);
    }

    public Map<String, Object> validateSign(Map<String, Object> request) {
        Optional<SignPayload> signPayload = getSignPayloadParams(request);

        if (signPayload.isEmpty()) return Map.of("statusCode",400);

        // validate sign

        // verify address and nonce with database

        // create cookie with data if 2 previous steps are correct

        return Map.of("statusCode",200, "body","Signature validated, redirect to showContent",
                "headers", Map.of("Set-Cookie", "the-cookie"));
    }

    public Map<String, Object> showContent(Map<String, Object> request) {
        PayloadCookie payloadCookie = getDecryptedCookie(request);

        if (payloadCookie.payload().isEmpty()) {
            return Map.of("statusCode",400);
        }

        if (payloadCookie.cookie().isEmpty()) {
            return Map.of("statusCode",200, "body","Redirect to login and clear cookie");
        }

        return Map.of("statusCode",200,"body","Foo bar",
                "headers", Map.of("Set-Cookie", payloadCookie.cookie().get()));
    }

    private PayloadCookie getDecryptedCookie(Map<String, Object> request) {
        Optional<Payload> payload = getPayloadParams(request);

        if (payload.isEmpty()) return new PayloadCookie(Optional.empty(), Optional.empty());

        Optional<String> cookie = getCookie(request);

        if (cookie.isEmpty()) return new PayloadCookie(payload, Optional.empty());

        return new PayloadCookie(payload, validateCookie(cookie.get(), payload.get().stakeAddress()));
    }

    private Optional<String> validateCookie(String encryptedCookie, String stakeAddress) {
        return Optional.of(encryptedCookie);
    }

    private void saveNonceAndAddress(String stakeAddress, String somerandomnonce) {}

    private Optional<Payload> getPayloadParams(Map<String, Object> request) {
        String body = (String) request.get("body");

        if (body == null) return Optional.empty();

        return Optional.of(gson.fromJson(body, Payload.class));
    }

    private Optional<SignPayload> getSignPayloadParams(Map<String, Object> request) {
        String body = (String) request.get("body");

        if (body == null) return Optional.empty();

        return Optional.of(gson.fromJson(body, SignPayload.class));
    }

    private Optional<String> getCookie(Map<String, Object> request) {
        List<String> cookies = (List<String>) request.get("cookies");

        if (cookies == null || cookies.isEmpty()) return Optional.empty();

        return Optional.of(cookies.getFirst());
    }

    private static record PayloadCookie(Optional<Payload> payload, Optional<String> cookie) {}
}
