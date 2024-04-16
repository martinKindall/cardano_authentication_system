package com.walruscode.cardano.Utils;

import java.util.HashMap;
import java.util.Map;

public class RouterUtils {

    public static Map<String, Object> getResponse(int status, String body) {
        Map<String, Object> response = new HashMap<>(Map.of("statusCode", status, "body", body));
        Map<String, Object> headers = new HashMap<>(Map.of("Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Methods", "OPTIONS, POST"));

        response.put("headers", headers);

        return response;
    }

    public static Map<String, Object> getResponse(int status, String body, String cookie) {
        Map<String, Object> response = getResponse(status, body);
        Map<String, Object> headers = (Map<String, Object>) response.get("headers");

        headers.put("Set-Cookie", cookie);

        return response;
    }
}
