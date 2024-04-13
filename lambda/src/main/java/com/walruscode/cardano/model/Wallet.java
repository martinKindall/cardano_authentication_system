package com.walruscode.cardano.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class Wallet {
    private String stakeAddress;
    private String nonce;

    public Wallet() {
        this.stakeAddress = "";
        this.nonce = "";
    }

    public Wallet(String stakeAddress, String nonce) {
        this.stakeAddress = stakeAddress;
        this.nonce = nonce;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("stake_address")
    public String getStakeAddress() {
        return stakeAddress;
    }

    @DynamoDbAttribute("nonce")
    public String getNonce() {
        return nonce;
    }

    public void setStakeAddress(String stakeAddress) {
        this.stakeAddress = stakeAddress;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
}
