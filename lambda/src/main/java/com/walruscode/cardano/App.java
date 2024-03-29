package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.dto.Payload;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class App {

    private static final Gson gson = new Gson();

    public Map<String, Object> onEvent(Map<String, Object> request) {
        Optional<Payload> payload = getParams(request);

        if (payload.isEmpty()) return Map.of("statusCode",400);

        Optional<String> cookie = getCookie(request);

        if (cookie.isEmpty()) {
            // generate nonce and save it to DB, referencing the stake address
            saveNonceAndAddress(payload.get().stakeAddress(), "somerandomnonce");

            // trigger authentication logic
            return Map.of("statusCode",200, "body","Needs authentication");
        }

        System.out.println("Incoming cookie: " + cookie);

        boolean isValidCookie = validateCookie(cookie.get(), payload.get().stakeAddress());

        if (!isValidCookie) {
            // trigger clear cookie and redirect to login
            return Map.of("statusCode",200, "body","Redirect to login and clear cookie");
        }

        return Map.of("statusCode",200,"body","Foo bar",
                "headers", Map.of("Set-Cookie", cookie.get()));
    }

    private boolean validateCookie(String cookie, String stakeAddress) { return false; }

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
}
