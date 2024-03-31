package com.walruscode.cardano.dto;

import java.util.Objects;

public record SignPayload(String stakeAddress, String key, String sign) {

    public SignPayload {
        Objects.requireNonNull(stakeAddress);
        Objects.requireNonNull(key);
        Objects.requireNonNull(sign);
    }
}

