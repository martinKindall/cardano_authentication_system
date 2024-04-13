package com.walruscode.cardano.services;

import com.walruscode.cardano.model.Wallet;
import com.walruscode.cardano.repositories.WalletRepository;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class WalletService {

    private final WalletRepository repository;

    public WalletService(WalletRepository repository) {
        this.repository = repository;
    }

    public boolean isValid(String messageStakeAddress, String payloadStakeAddress,
                           String messageNonce) {
        if (!Objects.equals(messageStakeAddress, payloadStakeAddress)) return false;

        Optional<Wallet> wallet = repository.find(messageStakeAddress);

        if (wallet.isEmpty()) return false;

        return !wallet.get().getNonce().equals(messageNonce);
    }

    public void saveWallet(String stakeAddress, String nonce, Instant instant) {
        repository.saveWallet(stakeAddress, nonce, instant);
    }
}
