package com.walruscode.cardano;

import java.util.Map;

public class App {

    public Map<String, Object> onEvent(Map<String, Object> request) {
        return Map.of("statusCode",200,"body","Foo bar");
    }
}
