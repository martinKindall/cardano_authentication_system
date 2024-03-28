package com.walruscode.cardano;

import java.util.List;
import java.util.Map;

public class App {

    public Map<String, Object> onEvent(Map<String, Object> request) {
        List<String> cookies = (List<String>) request.get("cookies");

        for (String cookie: cookies) {
            System.out.println("Incoming cookie: " + cookie);
        }

        return Map.of("statusCode",200,"body","Foo bar",
                "headers", Map.of("Set-Cookie", "language=this-is-a-great-cookie"));
    }
}
