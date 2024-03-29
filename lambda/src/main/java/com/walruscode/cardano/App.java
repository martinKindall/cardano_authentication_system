package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.dto.Payload;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {

    private final Gson gson;

    public App(Gson gson) {
        this.gson = gson;
    }

    public Map<String, Object> login(Map<String, Object> request) {
        PayloadCookie payloadCookie = getDecryptedCookie(request);

        if (payloadCookie.payload().isEmpty()) {
            return Map.of("statusCode",400);
        }

        if (payloadCookie.cookie().isEmpty()) {
            // generate nonce and save it to DB, referencing the stake address
            saveNonceAndAddress(payloadCookie.payload().get().stakeAddress(), "somerandomnonce");

            // trigger authentication logic
            return Map.of("statusCode",200, "body","Needs SignData");
        }

        return Map.of("statusCode",200, "body","Redirect to showContent");
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
        Optional<Payload> payload = getParams(request);

        if (payload.isEmpty()) return new PayloadCookie(Optional.empty(), Optional.empty());

        Optional<String> cookie = getCookie(request);

        if (cookie.isEmpty()) return new PayloadCookie(payload, Optional.empty());

        return new PayloadCookie(payload, validateCookie(cookie.get(), payload.get().stakeAddress()));
    }

    private Optional<String> validateCookie(String encryptedCookie, String stakeAddress) {
        return Optional.of(encryptedCookie);
    }

    private void saveNonceAndAddress(String stakeAddress, String somerandomnonce) {}

    private Optional<Payload> getParams(Map<String, Object> request) {
        String body = (String) request.get("body");

        if (body == null) return Optional.empty();

        return Optional.of(gson.fromJson(body, Payload.class));
    }

    private Optional<String> getCookie(Map<String, Object> request) {
        List<String> cookies = (List<String>) request.get("cookies");

        if (cookies == null || cookies.isEmpty()) return Optional.empty();

        return Optional.of(cookies.getFirst());
    }

    private static record PayloadCookie(Optional<Payload> payload, Optional<String> cookie) {}
}
