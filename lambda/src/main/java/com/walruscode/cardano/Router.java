package com.walruscode.cardano;

import com.google.gson.Gson;
import java.util.Map;

public class Router {
    private static final Gson gson = new Gson();
    private static final App app = new App(gson);

    public Map<String, Object> login(Map<String, Object> request) {
        return app.getAndSaveNonce(request);
    }

    public Map<String, Object> showContent(Map<String, Object> request) {
        return app.showContent(request);
    }
}
