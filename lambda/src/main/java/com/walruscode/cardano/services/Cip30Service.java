package com.walruscode.cardano.services;

import org.cardanofoundation.cip30.AddressFormat;
import org.cardanofoundation.cip30.CIP30Verifier;
import org.cardanofoundation.cip30.MessageFormat;

import java.util.Objects;
import java.util.Optional;

public class Cip30Service {

    public Optional<Cip30Result> verify(String key, String sign) throws Exception {
        var verifier = new CIP30Verifier(sign, key);

        var result = verifier.verify();

        return result.isValid() ? Optional.of(new Cip30Result(
                result.getMessage(MessageFormat.TEXT),
                result.getAddress(AddressFormat.TEXT).orElseThrow())) : Optional.empty();
    }

    public record Cip30Result(String message, String stakeAddress) {

        public Cip30Result {
            Objects.requireNonNull(message);
            Objects.requireNonNull(stakeAddress);
        }
    }
}
