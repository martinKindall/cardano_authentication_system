package com.walruscode.cardano;

import com.google.gson.Gson;
import com.walruscode.cardano.repositories.WalletRepository;
import com.walruscode.cardano.services.Cip30Service;
import com.walruscode.cardano.services.EncryptionService;
import com.walruscode.cardano.services.WalletService;
import shaded.com.google.common.base.Supplier;

import java.util.Map;
import java.util.Objects;

import static com.walruscode.cardano.Utils.RouterUtils.getResponse;

public class Router {
    private static final App app = new App(new Gson(), new Cip30Service(), new WalletService(new WalletRepository()),
            new EncryptionService(System.getenv("SECRET_KEY")));

    public Map<String, Object> login(Map<String, Object> request) {
        return optionsRequestMiddleware(request, () -> app.getAndSaveNonce(request));
    }

    public Map<String, Object> showContent(Map<String, Object> request) {
        return optionsRequestMiddleware(request, () -> app.showContent(request));
    }

    public Map<String, Object> validateSign(Map<String, Object> request) {
        return optionsRequestMiddleware(request, () -> app.validateSign(request));
    }

    private Map<String, Object> optionsRequestMiddleware(Map<String, Object> request, Supplier<Map<String, Object>> supplier) {
        Map<String, Object> requestContext = (Map<String, Object>) request.get("requestContext");
        Map<String, Object> http = (Map<String, Object>) requestContext.get("http");

        if (Objects.equals(http.get("method"), "OPTIONS")) {
            return getResponse(200, "");
        }

        return supplier.get();
    }
}
