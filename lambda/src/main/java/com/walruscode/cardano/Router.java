package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.services.Cip30Service;

import java.util.Map;

public class Router {
    private static final App app = new App(new Gson(), new Cip30Service());

    public Map<String, Object> login(Map<String, Object> request) {
        return app.getAndSaveNonce(request);
    }

    public Map<String, Object> showContent(Map<String, Object> request) {
        return app.showContent(request);
    }

    public Map<String, Object> validateSign(Map<String, Object> request) {
        return app.validateSign(request);
    }
}
