package com.walruscode.cardano.dto;

import java.util.Objects;

public record Payload(String stakeAddress) {

    public Payload {
        Objects.requireNonNull(stakeAddress);
    }
}